(ns ktoa.components
  #?(:cljs (:require-macros [ktoa.components :refer [gen-wrappers]]))
  #?(:cljs (:require [ktoa.core :as core])))

#?(:cljs
   (do
     (defn class [opt]
       (.createClass core/react (clj->js opt)))

     (defn element [element opts & children]
       (apply (.-createElement core/react) element (clj->js opts) (clj->js children)))

     (defn component [opts]
       (fn
         ([] (class opts))
         ([props & children] (element (class opts) props children))))

     (when core/react-native
       (gen-wrappers
        {"activity-indicator" "ActivityIndicator"
         "button" "Button"
         "date-picker-ios" "DatePickerIOS"
         "drawer-layout-android" "DrawerLayoutAndroid"
         "image" "Image"
         "keyboard-avoiding-view" "KeyboardAvoidingView"
         "list-view" "ListView"
         "map-view" "MapView"
         "modal" "Modal"
         "navigator" "Navigator"
         "picker" "Picker"
         "picker-ios" "PickerIOS"
         "progress-bar-android" "ProgressBarAndroid"
         "progress-view-ios" "ProgressViewIOS"
         "refresh-control" "RefreshControl"
         "scroll-view" "ScrollView"
         "segmented-control-ios" "SegmentedControlIOS"
         "slider" "Slider"
         "status-bar" "StatusBar"
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
         "web-view" "WebView"}))

     (defn list-view-ds []
       (let [ds (aget core/react-native "ListView" "DataSource")]
         (ds. #js {:rowHasChanged not=})))))

#?(:clj
   (defmacro gen-wrappers [comps]
     (let [wrap (fn[[name prop]] `(def ~(symbol name) (partial element (aget core/react-native ~prop))))]
       `(do ~@(map wrap comps)))))
