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
(def add-navigation-helpers (.-addNavigationHelpers ReactNavigation))
(def stack-navigator (.-StackNavigator ReactNavigation))
(def tab-navigator (.-TabNavigator ReactNavigation))




(defn random-color
  []
  (js* "'#'+('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6)"))

(comment)



(def style
  {
   :title       {:font-size   30
                 :font-weight "100"
                 :margin      20
                 :text-align  "center"}
   :button      {:background-color "#999"
                 :padding          10
                 :margin-bottom    20
                 :border-radius    5}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   })


(defn resd [props]
  (let [number (-> props (get "params") (get "number"))
        route-name "Index"]
    [view {:style {:align-items      "center"
                   :justify-content  "center"
                   :flex             1
                   :background-color (random-color)}}
     [view {:style {:background-color "rgba(256,256,256,0.5)"
                    :margin-bottom    20}}
      [text {:style (style :title)} "Card number " number]]
     [touchable-highlight
      {:style    (style :button)
       :on-press #(dispatch
                    [:nav/navigate
                     [#:nav.route {:key       (keyword (str number))
                                   :routeName :Card
                                   :params    {:number (inc number)}}
                      route-name]])}
      [text {:style (style :button-text)} "Next"]]
     [touchable-highlight {:on-press #(dispatch [:nav/reset route-name])
                           :style    (style :button)}
      [text {:style (style :button-text)} "RESET"]]]))

(defn settings []
  [view {:style {:flex 1
                 :justify-content "center"
                 :align-items "center"}}
   [text "SETTINGS"]])


(defn nav-app-root [{:keys [navigation]}]
  [view {:style {:flex-direction   "column"
                 :flex             1
                 :padding          40
                 :align-items      "center"
                 :background-color (random-color)}}
   [text {:style (style :title)} "Hejsan"]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 30}}]
   [touchable-highlight {:style    (style :button)
                         :on-press #(dispatch
                                     [:nav/navigate
                                      [#:nav.route {:key       :0
                                                    :routeName :Card
                                                    :params    {:number 1}}
                                       "Index"]])}
    [text {:style (style :button-text)} "press me"]]])


(defn nav-wrapper [component title]
  (let [comp (r/reactify-component
              (fn [{:keys [navigation]}]
                [component (-> navigation .-state js->clj)]))]
    (aset comp "navigationOptions" #js {"title" title})
    comp))


(def resd-comp (nav-wrapper resd #(str "Card "
                                       (aget % "state" "params" "number"))))

(def app-root-comp (nav-wrapper nav-app-root "Welcome"))

(def stack-router {:Home {:screen app-root-comp}
                   :Card {:screen resd-comp}})


(def sn (r/adapt-react-class (stack-navigator (clj->js stack-router))))

(defn card-start [] (let [nav-state (subscribe [:nav/stack-state "Index"])]
                      (fn []
                        (js/console.log @nav-state)
                        [sn {:navigation (add-navigation-helpers
                                          (clj->js
                                           {"dispatch" #(do
                                                          (js/console.log "EVENT" %)
                                                          (dispatch [:nav/js [% "Index"]]))
                                            "state"    (clj->js @nav-state)}))}])))

(def tab-router {:Index    {:screen (nav-wrapper card-start "Index")}
                 :Settings {:screen (nav-wrapper settings "Settings")}})

(defn tab-navigator-inst []
  (tab-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Settings"]
                                                :initialRouteName "Index"})))

(defn get-state [action]
  (-> (tab-navigator-inst)
      .-router
      (.getStateForAction action)))

(defonce tn
  (let [tni (tab-navigator-inst)]
    (aset tni "router" "getStateForAction" #(let [new-state (get-state %)]
                                              (js/console.log "STATE" % new-state)
                                              (dispatch [:nav/set new-state])
                                              new-state) #_(do (js/console.log %)
                                                               #_(get-state %)))
    (r/adapt-react-class tni)))

(defn start []
  (let [nav-state (subscribe [:nav/tab-state])]
    (fn []
      [tn])
    )
  )


(defn nav-init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "RnExamples" #(r/reactify-component start)))


(defn alert [title]
      (.alert (.-Alert ReactNative) title))


(defn view-pager-example []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view-pager {:style {:flex 1 :width 360 :height 180} :initial-page 0}

       [view {:style {:background-color (random-color) :margin 10 :align-items "center"} :key 1}
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]
        [touchable-highlight {:style {:background-color (random-color) :padding 10 :border-radius 5}
                              :on-press #(alert "HELLO!")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]
        [text "happen"]]

       [view {:style {:background-color (random-color) :padding 20 :align-items "center"  :width 200 :height 80} :key 2}
        [text {:style {:width 80 :height 80}} "pg1"]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]]

       [view {:style {:background-color (random-color) :padding 20 :align-items "center" :width 80 :height 80} :key 3}
        [text {:style {:width 80 :height 80}} "pg2"]]])))





(defn pager-app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view-pager {:style {:flex 1 :width 360 :height 180} :initial-page 0}

       [view {:style {:background-color (random-color) :margin 10 :align-items "center"} :key 1}
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]
        [touchable-highlight {:style {:background-color (random-color) :padding 10 :border-radius 5}
                              :on-press #(alert "HELLO!")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me2"]]
        [text "ha ha ha"]]

       [view {:style {:background-color (random-color) :padding 20 :align-items "center"  :width 200 :height 80} :key 2}
        [text {:style {:width 80 :height 80}} "pg1"]
        [image {:source logo-img
                :style  {:width 80 :height 80 :margin-bottom 30}}]]




       [view {:style {:background-color "#3df" :padding 20 :align-items "center" :width 80 :height 80} :key 3}

        [text {:style {:width 80 :height 80}} "pg2"]]])))





(defn app-root [] (pager-app-root))

(defn pager-init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "RnExamples" #(r/reactify-component pager-app-root)))

(defn init []
  (pager-init))
