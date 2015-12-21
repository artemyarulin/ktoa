;; Code looks a big ugly because it should work with advanced
;; optimization. React packager somehow spoils goog.require plus I
;; didn't want to use any externs to make it more interesting :)

;; All credits to https://github.com/decker405/figwheel-react-native

(ns ktoa.repl
  (:require [ktoa.core :as ktoa]))

(def config (atom {})) ;; too lazy to pass it as props to root component

(defn download [urls]
  (when (seq urls)
    (let [[url cb] (first urls)
          url' (str (:base-url @config) "/" url "?" (rand-int 1000))
          _ (.log js/console (str "Downloading: " url'))
          prom (-> (js/fetch url')
                   (.then #(.text %))
                   (.then #(do (cb %)
                               ;; Let's tell figwheel to get WS lib from the right place
                               (when (re-find #"figwheel.client.socket.js" url)
                                 (aset (aget js/figwheel "client" "socket") "get_websocket_imp" (fn[](js/require "WebSocket"))))
                               ;; <script> tags cannot be used for file reload. Let's put our own download instead
                               (when (re-find #"goog.net.jsloader" url)
                                 (aset (aget (js/eval "goog") "net" "jsloader") "load"
                                       (fn[load-url]
                                         (let [def (js/eval "new goog.async.Deferred()")]
                                           (download [[(str "goog/" load-url)
                                                       (fn[s](do (js/eval s)
                                                                 ((goog.bind (aget def "callback") def))))]])
                                           def))))
                               (download (rest urls)))))]
      ((goog.bind (aget prom "catch") prom) #(.error js/console %)))))

(defn start-figwheel []
  (download [["goog/base.js" js/eval]
             ["cljs_deps.js", js/eval]
             ["goog/deps.js", (fn[text]
                                (js/eval text)
                                (let [deps (atom [])
                                      loader #(do (swap! deps conj %) true)
                                      req (js/eval "goog.require")
                                      goog (js/eval "goog")]
                                  ;; Standard way of extending Clousure with our own loader
                                  (aset (aget goog "global") "CLOSURE_IMPORT_SCRIPT" loader)
                                  (req "figwheel.connect")
                                  (req (:root-ns @config))
                                  (download (map #(identity [(str "goog/" %) js/eval]) @deps))))]]))

(def root-component (ktoa/class {:render #(ktoa/text {:onPress (fn[](start-figwheel))} "Start figwheel")
                                 :componentWillMount #(start-figwheel)}))

(defn start-repl [{:keys [app-name base-url root-ns]
                  :or {:app-name "app"
                       :base-url "http://localhost:3449/js"
                       :root-ns "app.core"}}]
  (.log js/console (str "Starting REPL for app:" app-name))
  (reset! config {:base-url base-url :root-ns root-ns})
  (ktoa/register-component app-name (constantly root-component)))
