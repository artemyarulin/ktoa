(ns ktoa.core)

(def react-native
  "With fighweel-react-native approach we cannot simply do
  `require('react-navtie')` as react packager catches this import and
  convert module name to the full path. As we download new files and
  `eval` it by outselfs we avoid react packager. This variable
  requires `react-native` with a right path, abstracting this
  workaround for you. On non RN environments returns nilt"
  (when (exists? js/require)
    (js/require "react-native/Libraries/react-native/react-native.js")))

(def react-native-root
  "React gives root element index as a rootTag property when we
  register componenet using AppRegistry.registerRunnable. When we are
  in a development mode and would like to remount our component we
  don't have an access to rootTag, so we re-mount to index 1 as React
  starts with it. Keep in mind that if you have multipole RNRootView
  you may want to remount to inxed 2,3, etc"
  1)

(def modules (when react-native
               {:create-element (.-createElement react-native)
                :platform (.-Platform react-native)
                :registry (.-AppRegistry react-native)}))

(def os
  "Returns nil for non react-native environments or :ios or :android
  depending on current platform"
  (some-> modules :platform .-OS keyword))

(defn register! [app-name mount node]
  "If we have any app in registry - simply re-mount the app to the
   root node in order to reload it. If nothing exists yet - register
   in a usual way. If we are in a browser - mount to the browser node"
  (if react-native
    (if (seq (.getAppKeys (:registry modules)))
      (mount react-native-root)
      (.registerRunnable (:registry modules) app-name #(mount (aget % "rootTag"))))
    (mount (node))))

(defn class [opt]
  "Creates React class"
  (.createClass react-native (clj->js opt)))

(def register-component
  "Register the component"
  (when-let [registry (:registry modules)]
    (.-registerComponent registry)))

(when react-native
  (set! js/React react-native))
