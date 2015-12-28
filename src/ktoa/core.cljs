(ns ktoa.core)

(def react-native
  "We cannot load react-native directly as react-packager has a
  spechial handling of this. As for now we are using
  figwhee-react-native with js eval which avoids packager we have to
  load RN using this path. If we move to boot-react-native approach we
  can remove it"
  (when (exists? js/require)
    (js/require "react-native/Libraries/react-native/react-native.js")))

(def react-native-root
  "React gives root element index as a rootTag property when we
  register componenet as registerRunnable. When we are in a
  development mode and would like to remount our component we don't
  have an access to rootTag, so we re-mount to the first index. Keep in
  mind that if you have multipole RNRootView you may want to remount
  to second, third, etc. indexes"
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
