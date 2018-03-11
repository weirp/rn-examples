(ns rn-examples.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
 :nav/tab-state
 (fn [db _]
   (:nav/tab-state db)))

(reg-sub
 :nav/stack-state
 (fn [db [_ route-name]]
   (get-in db [:nav/stack-state (keyword "nav.routeName" route-name)])))
