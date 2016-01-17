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
