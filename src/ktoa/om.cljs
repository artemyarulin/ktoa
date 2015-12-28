(ns ktoa.om
  (:require [ktoa.core :as core]))

(def om-options
  "Om-Next reconciler accepts optional render and unmount function
  which has a different implementation in RN and browser React. In
  case non RN environments this function returns empty map so you can
  easiliy merge this options all the time"
  (if core/react-native
    {:root-render (.-render core/react-native)
     :root-unmount (.-unmountComponentAtNode core/react-native)}
    {}))

(defn set-global! []
  "Om-Next using React global under the hood. Currently there is no
  way how we can configure it, so here we set minumum React version"
  (when core/react-native
    (this-as self
             (aset self "React"
                   (js-obj "Component" (.-Component core/react-native)
                           "createElement" (.-createElement core/react-native))))))
