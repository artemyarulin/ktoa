(ns ktoa.components
  #?(:cljs (:require-macros [ktoa.components :refer [gen-wrappers]]))
  #?(:cljs (:require [ktoa.core :as core])))

#?(:cljs
   (defn element [element opts & children]
     "Helper for React Native component: Allows developer to use
      clojure maps as component options and add multiple
      children"
     (apply (.-createElement core/react) element (clj->js opts) children)))

#?(:clj
   (defmacro gen-wrappers [comps]
     (let [wrap (fn[[name prop]] `(def ~(symbol name) (partial element (aget core/react-native ~prop))))]
       `(do ~@(map wrap comps)))))

#?(:cljs
   (when core/react-native ;; Wrap every RN component with element helper and generate a def definition
     (gen-wrappers {"activity-indicator-ios" "ActivityIndicatorIOS"
                    "date-picker-ios" "DatePickerIOS"
                    "drawer-layout-android" "DrawerLayoutAndroid"
                    "image" "Image"
                    "list-view" "ListView"
                    "map-view" "MapView"
                    "modal" "Modal"
                    "navigator" "Navigator"
                    "picker-ios" "PickerIOS"
                    "progress-bar-android" "ProgressBarAndroid"
                    "progress-view-ios" "ProgressViewIOS"
                    "pull-to-refresh-view-android" "PullToRefreshViewAndroid"
                    "scroll-view" "ScrollView"
                    "segmented-control-ios" "SegmentedControlIOS"
                    "slider-ios" "SliderIOS"
                    "switch" "Switch"
                    "tab-bar-ios" "TabBarIOS"
                    "tab-bar-ios-item" "TabBarIOS.Item"
                    "text" "Text"
                    "text-input" "TextInput"
                    "toolbar-android" "ToolbarAndroid"
                    "touchable-highlight" "TouchableHighlight"
                    "touchable-native-feedback" "TouchableNativeFeedback"
                    "touchable-opacity" "TouchableOpacity"
                    "touchable-without-feedback" "TouchableWithoutFeedback"
                    "view" "View"
                    "view-pager-android" "ViewPagerAndroid"
                    "web-view" "WebView"})))
