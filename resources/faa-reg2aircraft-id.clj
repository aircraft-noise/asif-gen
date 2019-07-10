;;
;; Rules to translate from FAA aircraft registration database metadata to AEDT/ASIF aircraftId
;;

[[[[:airframe/model   (starts-with "707")]]
  "707120"]
 [[[:airframe/model   (starts-with "717")]]
  "717200"]
 [[[:airframe/model   (starts-with "727")]]
  "727200"]
 [[[:airframe/model   (starts-with "737-8")]]
  "737800"]
 [[[:airframe/model   (starts-with "737-9")]]
  "737800"]
 [[[:airframe/model   (starts-with "737")]]
  "737700"]
 [[[:airframe/model   (starts-with "747")]]
  "747400"]
 [[[:airframe/model   (starts-with "757")]]
  "757300"]
 [[[:airframe/model   (starts-with "767")]]
  "767300"]
 [[[:airframe/model   (starts-with "777")]]
  "777300"]
 [[[:airframe/model   (starts-with "787")]]
  "7878R"]
 [[[:airframe/model   (starts-with "A330")]]
  "A330-343"]
 [[[:airframe/model   (starts-with "A350")]]
  "A330-343"]
 [[[:airframe/model   (starts-with "A300")]]
  "A300-622R"]
 [[[:airframe/model   (starts-with "A321")]]
  "A321-232"]
 [[[:airframe/model   (starts-with "A320")]]
  "A320-232"]
 [[[:airframe/model   (starts-with "A319")]]
  "A319-131"]
 [[[:airframe/model   (starts-with "A380")]]
  "A380-861"]
 [[[:airframe/model   (starts-with "DC-8")]]
  "DC870"]
 [[[:airframe/model   (starts-with "DC-9")]]
  "DC950"]
 [[[:airframe/model   (starts-with "DC-10")]]
  "DC1030"]
 [[[:airframe/model   (starts-with "MD-10")]]
  "DC1030"]
 [[[:airframe/model   (starts-with "MD-10")]]
  "DC1030"]
 [[[:airframe/engines (equal-to 4)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (greater-than 465)]]
  "747400"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 400 570)]]
  "777300"]
 [[[:airframe/engines (equal-to 3)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 380 385)]]
  "DC1030"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 377 379)]]
  "A330-343"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 249 375)]]
  "767300"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 221, 240)]]
  "757300"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 178 182)]]
  "757300"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 128 171)]]
  "737800"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 50,100)]]
  "EMB190"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-prop)]
   [:airframe/seats   (in-range 30, 60)]]
  "DHC8"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 30 50)]]
  "EMB145"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 20 30)]]
  "GV"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-fan)]
   [:airframe/seats   (in-range 10 20)]]
  "LEAR35"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :turbo-prop)]
   [:airframe/seats   (in-range 10 30)]]
  "DHC6"]
 [[[:airframe/engines (equal-to 2)]
   [:airframe/type    (ends-with "multi-engine")]
   [:engine/type      (equal-to :reciprocating)]
   [:airframe/seats   (in-range 5 10)]]
  "BEC58P"]
 [[[:airframe/engines (equal-to 1)]
   [:airframe/type    (ends-with "single-engine")]
   [:engine/type      (equal-to :reciprocating)]
   [:airframe/seats   (in-range 0 4)]]
  "CNA172"]
 [[[:airframe/type    (equal-to :rotorcraft)]]
  "B407"]
 ]
