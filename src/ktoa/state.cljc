"Ring handler for the figwheel which persists state on a
disk. Whenever state has changed you may call this handler with a new
state and it will append a line to the specific file with a function
call like (reset! [atom-name] [new-state])

Handler expects POST request to /sync endpoint with query like
?path=path/to/file/state.cljs&tid=[transaction id]&id=[client id] query string and POST
body as a new state

- Macroses used as I wanted to make it really simple from the user point of view
- We have to have [transaction-id client-id] in order to avoid loading same client old
  state that could come from the server
- We have to keep last remote state in order to avoid sending it to the server

This is more or less PoC. If you find it useful and at the same time
paitful to use - create an issue and we will think about it "

(ns ktoa.state
  (:require [clojure.string :refer [split trim]]))

#?(:clj
   (do
     (defmacro persist [state]
       (let [path (-> state meta :file)
             cur-ns (-> state meta :ns)]
         `(do (defonce ~'_# (.log js/console (str "Persisting " ~(str state) " into " ~path)))
              (ktoa.state/start-watching ~state ~path)
              (defn ~'ktoa-update-state [id# tid# state#] (ktoa.state/apply-state ~state {:id id# :tid tid#} state#)))))

     (def token ";;GENERATED")

     (defn handler [req]
       (when (= (:uri req) "/sync")
         (let [new-state (slurp (:body req))
               params (into {} (map #(split % #"=") (split (:query-string req) #"&")))
               content (trim (first (split (slurp (params "path")) (re-pattern token))))]
           (spit (params "path")
                 (str content "\n" token "\n(ktoa-update-state " (params "id") " " (params "tid") " " new-state ")"))
           {:status 200})))))

#?(:cljs
   (do

     (defonce client (atom {:id (rand)
                            :tid 0
                            :state nil}))

     (defn apply-state [var sender data]
       (when (and (or (not (= (:id @client) (:id sender)))
                      (< (:tid @client) (:tid sender)))
                  (not= (:state @client) data))
         (swap! client assoc :state data)
         (reset! var data)))

     (defn send [from data path]
       (when (exists? js/figwheel)
         (let [ws-host (or (.-url @(aget js/figwheel "client" "socket" "socket_atom"))
                       "http://localhost:3449/ws")
               host (str (goog.uri.utils.getDomain ws-host) ":" (goog.uri.utils.getPort ws-host))
               url (str "http://" host "/sync?id=" (:id @from) "&tid=" (:tid @from) "&path=" path)
               req (js/XMLHttpRequest.)]
           (.open req "POST" url true)
           (.send req (str @data)))))

     (defn start-watching [obj path]
       (add-watch obj :persist-watcher
                  (fn[key atom old new]
                    ;; It's not equal in case if is local modification. If it's remote apply-state we don't want to re-send it back to the server
                    (when (not= (:state @client) new)
                      (swap! client update-in [:tid] inc)
                      (send client atom path)))))))
