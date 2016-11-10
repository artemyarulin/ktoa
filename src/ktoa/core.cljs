(ns ktoa.core)

(def react-native
  (when (exists? js/require)
    (js/require "react-native")))

(def react
  (when (exists? js/require)
    (js/require "react")))

(def modules
  (when react-native
    {:create-element (.-createElement react)
     :platform (.-Platform react-native)
     :registry (.-AppRegistry react-native)}))

(def os
  "Returns nil for non react-native environments or :ios or :android
  depending on current platform"
  (some-> modules :platform .-OS keyword))

(defn app-registered? [name]
  (-> (:registry modules)
      .getAppKeys
      (.indexOf name)
      (not= -1)))

(defn run-app!
  "May be called multiple times - if application already
  registerd (like during REPL session) it will restart application
  again with (optional) props"
  ([name comp] (run-app! name comp {}))
  ([name comp override-props]
   (let [registry (:registry modules)
         registered? (app-registered? name)]
     (.registerComponent registry name comp)
     (when registered?
       (.runApplication registry name (clj->js {:rootTag 1 :initialProps override-props}))))))
