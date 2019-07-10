(ns analyze.aedt
  (:require
   [clojure.string :as string]
   [clojure.set :as set]
   [clojure.java.io :as io]
   ))

(defn ^:private unkeywordify
  [x]
  (cond
    (keyword? x) (clojure.core/name x)
    (string? x) x
    :else ""))

;;
;; The following functions define the predicates that can be used in "rules"
;;

(defn ^:private starts-with
  "Returns function that tests if its argument starts-with pattern"
  [pattern]
  (fn [s]
    (string/starts-with? (unkeywordify s) pattern)))

(defn ^:private ends-with
  "Returns function that tests if its argument ends-with pattern"
  [pattern]
  (fn [s]
    (string/ends-with? (unkeywordify s) pattern)))

(defn ^:private equal-to
  "Returns function that tests if its argument equals pattern"
  [pattern]
  (fn [s]
    (= s pattern)))

(defn ^:private in-range
  "Returns function that tests if its argument lies with lower and upper bounds, INCLUSIVE"
  [lower upper]
  (fn [x]
    (<= lower x upper)))

(defn ^:private greater-than
  "Returns function that tests if its argument is greater than bound"
  [bound]
  (fn [x]
    (> x bound)))

;;
;; These functions process a collection of rules
;;

(defn ^:private process-alternative
  "Evaluates a sequence of tests, if all true, returns alt"
  [tests alt m]
  (if (nil? (seq tests))
    alt
    (let [[k predicate] (first tests)]
      (when-let [result (predicate (get m k))]
        (recur (next tests) alt m)))))

(defn ^:private process-alternatives
  "Evaluates a sequence of alternatives, if none returns (non-nil), return :unkown"
  [alts m]
  (if (nil? (seq alts))
    nil
    (let [[tests alt] (first alts)]
      (if-let [result (process-alternative tests alt m)]
        result
        (recur (next alts) m)))))

;;
;; Read in the rules from a resource file
;;

(def ^:private rules:faa-reg->aircraft-id
  (-> "faa-reg2aircraft-id.clj"
      io/resource
      slurp
      read-string
      eval))
;;
;; The public function...
;;

(defn faa-reg->aircraft-id
  "Returns AEDT aircraftId from FAA registration metadata"
  [aircraft-meta]
  (process-alternatives rules:faa-reg->aircraft-id aircraft-meta))

;; (def tm {:airframe/model "707foo"})
;; (def tm-bad {:airframe/model "foo"})
