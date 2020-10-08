var pXa = 0;
var pYa = 0;

var mapInfo = {
    titleName: "東湖廠6樓",
    x: 665,
    y: 430
};

var titleGroup = [
    //assy
    {lineName: "L1-1", x: 965, y: 265},
    {lineName: "L1-2", x: 855, y: 265},
    {lineName: "L2", x: 765, y: 265},
    {lineName: "L3", x: 700, y: 265},
    {lineName: "L4", x: 615, y: 265},
    {lineName: "L5", x: 515, y: 265},
    {lineName: "L6", x: 435, y: 265},
    {lineName: "L7", x: 355, y: 265}
];

//, straight: true, reverse: true
var testGroup = [
    {people: 3, x: 560, y: 450, reverse: true}, // group 37-39
    {people: 3, x: 560, y: 360, reverse: true}, // group 40-42
    {people: 3, x: 400, y: 450, reverse: true}, // group 35
    {people: 3, x: 400, y: 370, reverse: true}, // group 36
    {people: 2, x: 685, y: 360, reverse: true} // group 43-47
];

var babGroup = [
    {people: 6, x: 945, y: 85, lineName: "L1-1", straight: true, reverse: true},
    {people: 6, x: 870, y: 85, lineName: "L1-2", straight: true, reverse: true},
    {people: 6, x: 770, y: 85, lineName: "L2", straight: true, reverse: true},
    {people: 6, x: 700, y: 85, lineName: "L3", straight: true, reverse: true},
    {people: 6, x: 605, y: 85, lineName: "L4", straight: true, reverse: true},
    {people: 6, x: 540, y: 85, lineName: "L5", straight: true, reverse: true},
    {people: 6, x: 445, y: 85, lineName: "L6", straight: true, reverse: true},
    {people: 6, x: 380, y: 85, lineName: "L7", straight: true, reverse: true}
];

var fqcGroup = [
//    {people: 1, x: 410, y: 370, lineName: "FQC_1"},
//    {people: 1, x: 410, y: 440, lineName: "FQC_2"}
];

var minTestTableNo = 1;
var maxTestTableNo = 14;

var sitefloor = 6;

