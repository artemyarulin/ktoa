;; Code looks a big ugly because it should work with advanced
;; optimization. React packager somehow spoils goog.require
;; All credits to https://github.com/decker405/figwheel-react-native

(ns ktoa.repl
  #?(:cljs (:require [ktoa.core :as core]
                     [ktoa.components :as components])))

#?(:cljs (do

(def config (atom {}))

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
                                      loader #(do (.log js/console "Loader called for: " %)
                                                  (swap! deps conj %)
                                                  true)
                                      req (js/eval "goog.require")
                                      goog (js/eval "goog")]
                                  ;; Standard way of extending Clousure with our own loader
                                  (aset (aget goog "global") "CLOSURE_IMPORT_SCRIPT" loader)
                                  (.log js/console "Starting figwheel")
                                  (req "figwheel.connect")
                                  (.log js/console (str "Starting namespace: " (:root-ns @config)))
                                  (req (:root-ns @config))
                                  (.log js/console (str "Loading " (count @deps) " deps"))
                                  (download (map #(identity [(str "goog/" %) js/eval]) @deps))))]]))

(defn root-component []
  (components/class {:render #(components/text {:onPress (fn[](start-figwheel))} "Start figwheel")
                     :componentWillMount #(start-figwheel)}))

(defn start [{:keys [app-name base-url root-ns modules req-modules]}]
  (if-not (core/app-registered? app-name)
    (do
      (.log js/console (str "Starting REPL for app " app-name))
      (reset! config {:base-url base-url :root-ns root-ns})
      (let [deps (zipmap modules req-modules)
            orig-req js/require]
        (aset js/window "require" #(or (get deps %) (orig-req %))))
      (core/run-app! app-name root-component))
    (.log js/console (str "App with same name already registered. Check your config" app-name))))))

#?(:clj
   (defmacro start-repl [config]
     (let [req (fn [n] `(js/require ~n))]
       ;; As RN require is more like derective, rather than a function we have to call it from the global scope
       `(do (def ~'required-modules (vector ~@(map req (:modules config))))
            (repl/start (assoc ~config :req-modules ~'required-modules))))))
