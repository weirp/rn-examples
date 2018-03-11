(ns rn-examples.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [rn-examples.events]
            [rn-examples.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(def view-pager (r/adapt-react-class (.-ViewPagerAndroid ReactNative)))
(def NativeModules (.-NativeModules ReactNative))

;; from https://github.com/vikeri/re-navigate/blob/master/src/re_navigate/core.cljs
(def ReactNavigation (js/require "react-navigation"))
(def stack-navigator (r/adapt-react-class (.-StackNavigator ReactNavigation)))
(def add-navigation-helpers (.-addNavigationHelpers ReactNavigation))
(def stack-navigator (.-StackNavigator ReactNavigation))
(def tab-navigator (.-TabNavigator ReactNavigation))


(defn random-color
  []
  (js* "'#'+('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6)"))

(comment)

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn view-pager-example []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view-pager {:style {:flex 1 :width 360 :height 180} :initial-page 0}

       [view {:style {:background-color "#1f1" :margin 10 :align-items "center"} :key 1}
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]
        [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                              :on-press #(alert "HELLO!")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me2"]]
        [text "ha ha"]]

       [view {:style {:background-color "#da3" :padding 20 :align-items "center"  :width 200 :height 80} :key 2}
        [text {:style {:width 80 :height 80}} "pg1"]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]]

       [view {:style {:background-color "#3df" :padding 20 :align-items "center" :width 80 :height 80} :key 3}
        [text {:style {:width 80 :height 80}} "pg2"]]])))

(defn view-page2 []
  )

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view-pager {:style {:flex 1 :width 360 :height 180} :initial-page 0}

       [view {:style {:background-color "#1f1" :margin 10 :align-items "center"} :key 1}
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]
        [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                              :on-press #(alert "HELLO!")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me2"]]
        [text "ha ha"]]

       [view {:style {:background-color "#da3" :padding 20 :align-items "center"  :width 200 :height 80} :key 2}
        [text {:style {:width 80 :height 80}} "pg1"]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]]




       [view {:style {:background-color "#3df" :padding 20 :align-items "center" :width 80 :height 80} :key 3}

        [text {:style {:width 80 :height 80}} "pg2"]]])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "RnExamples" #(r/reactify-component app-root)))
