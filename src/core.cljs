(ns ktoa.core)

(def modules (when (exists? js/require)
               {:react-element (js/require "ReactElement")
                :platform (js/require "Platform")
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
