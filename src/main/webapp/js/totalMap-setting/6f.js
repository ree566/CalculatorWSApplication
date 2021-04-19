var pXa = 0;
var pYa = 0;

var mapInfo = {
    titleName: "東湖廠6樓",
    x: 925,
    y: 20
};

var titleGroup = [
    //assy
    {lineName: "L1-1", x: 180, y: 160},
    {lineName: "L1-2", x: 280, y: 160},
    {lineName: "L2", x: 385, y: 160},
    {lineName: "L3", x: 465, y: 160},
    {lineName: "L4", x: 550, y: 160},
    {lineName: "L5", x: 620, y: 160},
    {lineName: "L6", x: 700, y: 160},
    {lineName: "L7", x: 780, y: 160}
];

//, straight: true, reverse: true
var testGroup = [
    {people: 3, x: 490, y: 50}, // group 37-39
    {people: 5, x: 635, y: 50}, // group 40-42
    {people: 5, x: 855, y: 260, straight: true, reverse: true}, // group 35
    {people: 5, x: 935, y: 260, straight: true, reverse: true}, // group 36
    {people: 2, x: 440, y: 115}, // group 43-47
    {people: 2, x: 545, y: 115}, // group 43-47
    {people: 2, x: 620, y: 115} // group 43-47
];

var babGroup = [
    {people: 6, x: 210, y: 235, lineName: "L1-1", straight: true},
    {people: 6, x: 310, y: 235, lineName: "L1-2", straight: true},
    {people: 6, x: 390, y: 235, lineName: "L2", straight: true},
    {people: 6, x: 470, y: 235, lineName: "L3", straight: true},
    {people: 6, x: 555, y: 235, lineName: "L4", straight: true},
    {people: 6, x: 640, y: 235, lineName: "L5", straight: true},
    {people: 6, x: 710, y: 235, lineName: "L6", straight: true},
    {people: 6, x: 790, y: 235, lineName: "L7", straight: true}
];

var fqcGroup = [
//    {people: 1, x: 410, y: 370, lineName: "FQC_1"},
//    {people: 1, x: 410, y: 440, lineName: "FQC_2"}
];

var minTestTableNo = 1;
var maxTestTableNo = 24;

var sitefloor = 6;

