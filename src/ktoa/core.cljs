(ns ktoa.core)

(def modules (when (exists? js/require)
               {:react-element (js/require "ReactElement")
                :react (js/require "ReactNative")
                :platform (js/require "Platform")
                :registry (js/require "AppRegistry")
                :view (js/require "View")
                :text (js/require "Text")
                :text-input (js/require "TextInput")}))

(defn element [element opts & children]
  (apply (.-createElement (:react-element modules)) element (clj->js opts) children))

(def react-native-root 1) ;; Due some magic, RN root element has an index 1
(def react-native? (some? (:react-element modules)))
(def os (some-> modules :platform .-OS keyword))
(def version (some-> modules :platform .-Version keyword))

;; Components
(def view (partial element (:view modules)))
(def text (partial element (:text modules)))
(def text-input (partial element (:text-input modules)))

(def register-component
  (when-let [registry (:registry modules)]
    (aget registry "registerComponent")))

(defn register! [app-name mount node]
  "If we have any app in registry - simply re-mount the app to the root node,
   we need it for the REPL staff. If nothing exists yet - register in
   a usual way. If we are in a browser - mount to the browser node"
  (if react-native?
    (if (seq (.getAppKeys (:registry modules)))
      (mount react-native-root)
      (.registerRunnable (:registry modules) app-name #(mount (aget % "rootTag"))))
    (mount (node))))

(def om-options
  (if react-native?
    {:root-render (.-render (:react modules))
     :root-unmount (.-unmountComponentAtNode (:react modules))}
    {}))

(defn class [m]
  (when-let [react (:react modules)]
    ((aget react "createClass") (clj->js m))))

(when react-native?
  (this-as self
           (aset self "React" (js-obj "Component" (:react-element modules)
                                      "createElement" (.-createElement (:react-element modules))))))
