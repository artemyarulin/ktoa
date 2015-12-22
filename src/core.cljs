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

(def register-component (aget (:registry modules) "registerComponent"))

(defn register! [name mount]
  (let [registered? (seq (.getAppKeys (:registry modules)))
        root-tag 1]
    (if registered?
      (mount root-tag)
      (.registerRunnable (:registry modules) #(mount (.rootTag %))))))

(defn shim-react! []
  (aset (aget (js/eval "goog") "global") "React" (js-obj "Component" (:react-element modules)
                                                         "createElement" (.-createElement (:react-element modules)))))

(def om-options
  {:root-render (.-render (:react modules))
   :root-unmount (.-unmountComponentAtNode (:react modules))})

(defn class [m]
  ((aget (:react modules) "createClass") (clj->js m)))
