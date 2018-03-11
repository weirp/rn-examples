(ns rn-examples.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::greeting string?)

(s/def :nav.route/key keyword?)
(s/def :nav.route/routeName keyword?)
(s/def :nav.route/path keyword?)
(s/def :nav.route/param (s/or :str string? :num number?))
(s/def :nav.route/params (s/map-of keyword? :nav.route/param))
(s/def :nav/route (s/keys :req [:nav.route/key :nav.route/routeName]
                          :opt [:nav.route/path :nav.route/params]))
(s/def :nav.state/routes (s/coll-of :nav/route :kind vector?))
(s/def :nav.state/index integer?)
(s/def :nav/tab-state (s/keys :req [:nav.state/index :nav.state/routes] ))

(s/def ::app-db
  (s/keys :req-un [::greeting]
          :req [:nav/tab-state]))



;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :nav/tab-state   #:nav.state{:index  0
                                          :routes [#:nav.route{:key :IndexKey :routeName :Index}
                                                   #:nav.route{:key :SettingsKey :routeName :Settings}]}
             :nav/stack-state #:nav.routeName {:Index #:nav.state {:index  0
                                                                   :routes [#:nav.route {:key :Home :routeName :Home}]}}})
