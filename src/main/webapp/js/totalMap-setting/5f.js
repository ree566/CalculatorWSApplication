var pXa = -0;
var pYa = -0;

var mapInfo = {
    titleName: "東湖廠5樓",
    x: 5,
    y: 435
};

var titleGroup = [
    //assy
//    {lineName: "LA", x: 1090, y: 50},
//    {lineName: "LB", x: 1090, y: 160},
//    {lineName: "LC-1", x: 1110, y: 340},
//    {lineName: "LC-2", x: 1110, y: 405},
//    {lineName: "LD", x: 730, y: 160},
//    {lineName: "LE", x: 735, y: 80},
//    //pkg
//    {lineName: "LF", x: 300, y: 40},
//    {lineName: "LG", x: 210, y: 40},
//    {lineName: "LH", x: 125, y: 40},
//    {lineName: "5F_CELL", x: 1060, y: 275}
//    {lineName: "FQC", x: 210, y: 240}
];

var testGroup = [
    {people: 5, x: 1015, y: 205, straight: true, reverse: true}, // group 11-18
    {people: 5, x: 1080, y: 205, straight: true, reverse: true}, // group 6-10
    {people: 2, x: 1125, y: 275, straight: true}, // group 1-5
    {people: 4, x: 1125, y: 340, straight: true}, // group 1-5
    {people: 4, x: 1015, y: 40, straight: true, reverse: true} // group 1-5
];

var babGroup = [
//    {people: 4, x: 930, y: 55, lineName: "LA"},
//    {people: 4, x: 930, y: 170, lineName: "LB"},
//    {people: 3, x: 1000, y: 345, lineName: "LC-1"},
//    {people: 3, x: 1000, y: 410, lineName: "LC-2"},
//    {people: 3, x: 795, y: 170, lineName: "LD"},
//    {people: 3, x: 795, y: 55, lineName: "LE"},
//    {people: 3, x: 320, y: 100, lineName: "LF", straight: true, reverse: true},
//    {people: 3, x: 230, y: 100, lineName: "LG", straight: true, reverse: true},
//    {people: 3, x: 160, y: 100, lineName: "LH", straight: true, reverse: true},
//    {people: 6, x: 850, y: 290, lineName: "5F_CELL"}
];

var fqcGroup = [
//    {people: 1, x: 300, y: 215, lineName: "FQC_3"},
//    {people: 1, x: 300, y: 255, lineName: "FQC_4"},
//    {people: 1, x: 300, y: 300, lineName: "FQC_5"}
];

var minTestTableNo = 15;
var maxTestTableNo = 34;

var sitefloor = 5;