# ktoa

Set of useful helpers and wrappers around React Native for ClojureScript development. Handy in case you want to write cross platform Om-Next components

## Example

``` clojure

(ns app.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [ktoa.core :as ktoa]))

(defn render-browser []
  (dom/div nil
           (dom/span nil "What")
           (dom/span nil "is")
           (dom/span nil "ktoa?")
           (dom/span nil "Dunno")))

(defn render-mobile []
  (ktoa/view nil
             (ktoa/text nil "What")
             (ktoa/text nil "is")
             (ktoa/text nil "ktoa?")
             (ktoa/text nil "Dunno")))

(defui RootViewRN
  Object
  (render [this] (if ktoa/react-native?
                   (render-mobile)
                   (render-browser))))

(om/add-root! (om/reconciler {})
              RootViewRN (if ktoa/react-native?
                           ktoa/react-native-root
                           (.querySelector js/document "#app")))
```
