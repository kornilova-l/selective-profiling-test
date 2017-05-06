/**
 * Created by Liudmila Kornilova
 * on 05.05.17
 */

class Thread {
    static get layerColors() {
        return ["#5457FF", "#546EFF", "#547DFF", "#5492FF", "#549DFF", "#54ADFF", "#54C1FF"];
    }

    // static layerColors =
    constructor(tree) {
        this.tree = tree; // tree of calls
        this.depth = this.countDepth(this.tree, 0);
        this.layerHeight = $("#tree").height() / this.depth;
        this.layerHeight = Math.min(35, this.layerHeight);
    }

    /**
     * Create hierarchical <ol> tree in html element
     * @param htmlElement element in which tree will be created
     */
    createList(htmlElement) {
        console.log(this.tree);
        this.recursivelyCreateList_(htmlElement, this.tree, 0);
        $('ol').css("height", this.layerHeight * 2 + "px");
        $('li').css("height", this.layerHeight + "px");
        $('.method-name').css("line-height", this.layerHeight + "px");
    }

    /**
     * Build hierarchical list of calls for one thread
     * @param htmlElement current <li> element which may be appended with <ol> (child calls)
     * @param subTree
     * @param depth
     */
    recursivelyCreateList_(htmlElement, subTree, depth) {
        if (subTree.calls.length === 0) {
            return;
        }
        const duration = subTree.finishTime - subTree.startTime;
        const ol = $("<ol></ol>").appendTo(htmlElement);
        for (let i = 0; i < subTree.calls.length; i++) { // do not use for-in because order is important
            const childCall = subTree.calls[i];
            const newLi = $("<li><p class='method-name'>" + childCall.methodName + "</p></li>").appendTo(ol);
            const childDuration = childCall.finishTime - childCall.startTime;
            $(Thread.generatePopup(childCall.methodName, childCall.startTime, childDuration)).appendTo(newLi);
            newLi.bind("mouseover", (e) => {
                // console.log('in', e);
                newLi.children().eq(1).css("display", "block").css("top", 0).css("left", 0);
                console.log("in", e);
                console.log(newLi);
                console.log(newLi.last());
                return false;
            });
            newLi.bind("mouseout", (e) => {
                // console.log('in', e);
                newLi.children().eq(1).css("display", "none");
                console.log("out", e);
                return false;
            });
            const left = (childCall.startTime - subTree.startTime) / duration;
            newLi.css("left", (left * 100) + "%");
            newLi.css("width", (childDuration) / duration * 100 + "%");
            newLi.css("background-color", Thread.layerColors[Math.min(depth, Thread.layerColors.length - 1)]);
            this.recursivelyCreateList_(newLi, childCall, depth + 1);
        }
    }

    countDepth(subTree, depth) {
        if (subTree.calls.length !== 0) {
            depth = this.countDepth(subTree.calls[0], depth + 1)
        }
        return depth;
    }
    static generatePopup(methodName, startTime, duration) {
        return '<div class="detail"><h3>' + methodName +  '</h3>' +
            '<p>Start time: ' + Math.round(startTime / 10000) / 100 + ' ms</p>' +
            '<p>Duration: ' + Math.round(duration / 10000) / 100 + ' ms</p></div>';
    }
}

$(window).on("load", function () {
    const threads = {
        182478697: {
            "calls": [{
                "calls": [{
                    "calls": [{
                        "calls": [{
                            "calls": [{
                                "calls": [{
                                    "calls": [{
                                        "calls": [{
                                            "calls": [{
                                                "calls": [{
                                                    "calls": [],
                                                    "finishTime": 50059836,
                                                    "methodName": "doFinish",
                                                    "startTime": 49961005
                                                }], "finishTime": 50065029, "methodName": "abc", "startTime": 46711753
                                            }], "finishTime": 50068594, "methodName": "xyz", "startTime": 42577245
                                        }], "finishTime": 50072079, "methodName": "def", "startTime": 9873146
                                    }], "finishTime": 50075088, "methodName": "xyz", "startTime": 7575879
                                }], "finishTime": 50078009, "methodName": "def", "startTime": 7394465
                            }], "finishTime": 50080806, "methodName": "abc", "startTime": 3540211
                        }], "finishTime": 50084016, "methodName": "def", "startTime": 1427222
                    }], "finishTime": 50086722, "methodName": "abc", "startTime": 1371448
                }], "finishTime": 50089415, "methodName": "start", "startTime": 0
            }, {
                "calls": [{
                    "calls": [{
                        "calls": [{
                            "calls": [{
                                "calls": [{
                                    "calls": [{
                                        "calls": [{
                                            "calls": [{
                                                "calls": [{
                                                    "calls": [],
                                                    "finishTime": 76370207,
                                                    "methodName": "doFinish",
                                                    "startTime": 71335504
                                                }], "finishTime": 76394554, "methodName": "xyz", "startTime": 71250499
                                            }], "finishTime": 76399918, "methodName": "def", "startTime": 70091057
                                        }], "finishTime": 76403097, "methodName": "xyz", "startTime": 65916567
                                    }], "finishTime": 76405959, "methodName": "def", "startTime": 61732456
                                }], "finishTime": 76408926, "methodName": "abc", "startTime": 59601020
                            }], "finishTime": 76411685, "methodName": "xyz", "startTime": 57420699
                        }], "finishTime": 76439204, "methodName": "def", "startTime": 53286864
                    }], "finishTime": 76749901, "methodName": "abc", "startTime": 50188235
                }], "finishTime": 76754264, "methodName": "start", "startTime": 50150365
            }], "threadName": "pool-1-thread-3", "startTime": 0, "finishTime": 76754264
        },
        990623457: {
            "calls": [{
                "calls": [{
                    "calls": [{
                        "calls": [{
                            "calls": [{
                                "calls": [],
                                "finishTime": 10231453,
                                "methodName": "doFinish",
                                "startTime": 10180314
                            }], "finishTime": 10264603, "methodName": "xyz", "startTime": 9083155
                        }], "finishTime": 10271107, "methodName": "def", "startTime": 3183642
                    }], "finishTime": 10275160, "methodName": "abc", "startTime": 1904203
                }], "finishTime": 10278669, "methodName": "start", "startTime": 0
            }, {
                "calls": [{
                    "calls": [{
                        "calls": [{
                            "calls": [{
                                "calls": [{
                                    "calls": [{
                                        "calls": [{
                                            "calls": [{
                                                "calls": [{
                                                    "calls": [{
                                                        "calls": [{
                                                            "calls": [{
                                                                "calls": [{
                                                                    "calls": [{
                                                                        "calls": [{
                                                                            "calls": [{
                                                                                "calls": [{
                                                                                    "calls": [{
                                                                                        "calls": [{
                                                                                            "calls": [{
                                                                                                "calls": [{
                                                                                                    "calls": [{
                                                                                                        "calls": [{
                                                                                                            "calls": [{
                                                                                                                "calls": [{
                                                                                                                    "calls": [{
                                                                                                                        "calls": [{
                                                                                                                            "calls": [{
                                                                                                                                "calls": [{
                                                                                                                                    "calls": [{
                                                                                                                                        "calls": [{
                                                                                                                                            "calls": [{
                                                                                                                                                "calls": [{
                                                                                                                                                    "calls": [{
                                                                                                                                                        "calls": [{
                                                                                                                                                            "calls": [{
                                                                                                                                                                "calls": [{
                                                                                                                                                                    "calls": [{
                                                                                                                                                                        "calls": [{
                                                                                                                                                                            "calls": [{
                                                                                                                                                                                "calls": [{
                                                                                                                                                                                    "calls": [{
                                                                                                                                                                                        "calls": [{
                                                                                                                                                                                            "calls": [{
                                                                                                                                                                                                "calls": [{
                                                                                                                                                                                                    "calls": [{
                                                                                                                                                                                                        "calls": [],
                                                                                                                                                                                                        "finishTime": 118983910,
                                                                                                                                                                                                        "methodName": "doFinish",
                                                                                                                                                                                                        "startTime": 118803067
                                                                                                                                                                                                    }],
                                                                                                                                                                                                    "finishTime": 118988047,
                                                                                                                                                                                                    "methodName": "def",
                                                                                                                                                                                                    "startTime": 117561274
                                                                                                                                                                                                }],
                                                                                                                                                                                                "finishTime": 118990050,
                                                                                                                                                                                                "methodName": "xyz",
                                                                                                                                                                                                "startTime": 113350235
                                                                                                                                                                                            }],
                                                                                                                                                                                            "finishTime": 118991888,
                                                                                                                                                                                            "methodName": "abc",
                                                                                                                                                                                            "startTime": 112131710
                                                                                                                                                                                        }],
                                                                                                                                                                                        "finishTime": 118993810,
                                                                                                                                                                                        "methodName": "def",
                                                                                                                                                                                        "startTime": 110961447
                                                                                                                                                                                    }],
                                                                                                                                                                                    "finishTime": 118995621,
                                                                                                                                                                                    "methodName": "abc",
                                                                                                                                                                                    "startTime": 110812989
                                                                                                                                                                                }],
                                                                                                                                                                                "finishTime": 118997935,
                                                                                                                                                                                "methodName": "def",
                                                                                                                                                                                "startTime": 109587632
                                                                                                                                                                            }],
                                                                                                                                                                            "finishTime": 119000063,
                                                                                                                                                                            "methodName": "abc",
                                                                                                                                                                            "startTime": 105335978
                                                                                                                                                                        }],
                                                                                                                                                                        "finishTime": 119002186,
                                                                                                                                                                        "methodName": "xyz",
                                                                                                                                                                        "startTime": 101100545
                                                                                                                                                                    }],
                                                                                                                                                                    "finishTime": 119003777,
                                                                                                                                                                    "methodName": "def",
                                                                                                                                                                    "startTime": 96872134
                                                                                                                                                                }],
                                                                                                                                                                "finishTime": 119005387,
                                                                                                                                                                "methodName": "xyz",
                                                                                                                                                                "startTime": 94600192
                                                                                                                                                            }],
                                                                                                                                                            "finishTime": 119006942,
                                                                                                                                                            "methodName": "abc",
                                                                                                                                                            "startTime": 92419863
                                                                                                                                                        }],
                                                                                                                                                        "finishTime": 119008557,
                                                                                                                                                        "methodName": "def",
                                                                                                                                                        "startTime": 92283580
                                                                                                                                                    }],
                                                                                                                                                    "finishTime": 119010119,
                                                                                                                                                    "methodName": "abc",
                                                                                                                                                    "startTime": 91107548
                                                                                                                                                }],
                                                                                                                                                "finishTime": 119011598,
                                                                                                                                                "methodName": "def",
                                                                                                                                                "startTime": 91034136
                                                                                                                                            }],
                                                                                                                                            "finishTime": 119013139,
                                                                                                                                            "methodName": "abc",
                                                                                                                                            "startTime": 90909359
                                                                                                                                        }],
                                                                                                                                        "finishTime": 119015060,
                                                                                                                                        "methodName": "xyz",
                                                                                                                                        "startTime": 86733200
                                                                                                                                    }],
                                                                                                                                    "finishTime": 119016486,
                                                                                                                                    "methodName": "def",
                                                                                                                                    "startTime": 83541876
                                                                                                                                }],
                                                                                                                                "finishTime": 119017972,
                                                                                                                                "methodName": "xyz",
                                                                                                                                "startTime": 81288126
                                                                                                                            }],
                                                                                                                            "finishTime": 119028561,
                                                                                                                            "methodName": "abc",
                                                                                                                            "startTime": 77936252
                                                                                                                        }],
                                                                                                                        "finishTime": 119030316,
                                                                                                                        "methodName": "def",
                                                                                                                        "startTime": 74081303
                                                                                                                    }],
                                                                                                                    "finishTime": 119031869,
                                                                                                                    "methodName": "xyz",
                                                                                                                    "startTime": 72866221
                                                                                                                }],
                                                                                                                "finishTime": 119033368,
                                                                                                                "methodName": "def",
                                                                                                                "startTime": 72782683
                                                                                                            }],
                                                                                                            "finishTime": 119034779,
                                                                                                            "methodName": "abc",
                                                                                                            "startTime": 71648166
                                                                                                        }],
                                                                                                        "finishTime": 119036241,
                                                                                                        "methodName": "def",
                                                                                                        "startTime": 67520003
                                                                                                    }],
                                                                                                    "finishTime": 119037554,
                                                                                                    "methodName": "xyz",
                                                                                                    "startTime": 67429266
                                                                                                }],
                                                                                                "finishTime": 119038790,
                                                                                                "methodName": "abc",
                                                                                                "startTime": 65293803
                                                                                            }],
                                                                                            "finishTime": 119040117,
                                                                                            "methodName": "def",
                                                                                            "startTime": 62124846
                                                                                        }],
                                                                                        "finishTime": 119042497,
                                                                                        "methodName": "xyz",
                                                                                        "startTime": 60992709
                                                                                    }],
                                                                                    "finishTime": 119043686,
                                                                                    "methodName": "abc",
                                                                                    "startTime": 56860187
                                                                                }],
                                                                                "finishTime": 119044927,
                                                                                "methodName": "xyz",
                                                                                "startTime": 52708985
                                                                            }],
                                                                            "finishTime": 119046128,
                                                                            "methodName": "abc",
                                                                            "startTime": 49535886
                                                                        }],
                                                                        "finishTime": 119047333,
                                                                        "methodName": "def",
                                                                        "startTime": 46351345
                                                                    }],
                                                                    "finishTime": 119048457,
                                                                    "methodName": "xyz",
                                                                    "startTime": 43172706
                                                                }],
                                                                "finishTime": 119049574,
                                                                "methodName": "def",
                                                                "startTime": 39918789
                                                            }],
                                                            "finishTime": 119050697,
                                                            "methodName": "xyz",
                                                            "startTime": 38758278
                                                        }],
                                                        "finishTime": 119051790,
                                                        "methodName": "def",
                                                        "startTime": 37624080
                                                    }],
                                                    "finishTime": 119052828,
                                                    "methodName": "abc",
                                                    "startTime": 35480411
                                                }], "finishTime": 119053855, "methodName": "def", "startTime": 32292858
                                            }], "finishTime": 119055465, "methodName": "xyz", "startTime": 23552956
                                        }], "finishTime": 119056426, "methodName": "abc", "startTime": 19350047
                                    }], "finishTime": 119057382, "methodName": "xyz", "startTime": 17996510
                                }], "finishTime": 119058342, "methodName": "abc", "startTime": 15809298
                            }], "finishTime": 119059289, "methodName": "xyz", "startTime": 12662708
                        }], "finishTime": 119060248, "methodName": "def", "startTime": 11543626
                    }], "finishTime": 119061135, "methodName": "abc", "startTime": 10441699
                }], "finishTime": 119062038, "methodName": "start", "startTime": 10393993
            }], "threadName": "pool-1-thread-1"
        },
        2059622782: {
            "calls": [{
                "calls": [{
                    "calls": [{
                        "calls": [{
                            "calls": [{
                                "calls": [{
                                    "calls": [{
                                        "calls": [{
                                            "calls": [{
                                                "calls": [{
                                                    "calls": [{
                                                        "calls": [{
                                                            "calls": [{
                                                                "calls": [{
                                                                    "calls": [{
                                                                        "calls": [{
                                                                            "calls": [{
                                                                                "calls": [{
                                                                                    "calls": [{
                                                                                        "calls": [{
                                                                                            "calls": [{
                                                                                                "calls": [{
                                                                                                    "calls": [{
                                                                                                        "calls": [{
                                                                                                            "calls": [{
                                                                                                                "calls": [{
                                                                                                                    "calls": [{
                                                                                                                        "calls": [{
                                                                                                                            "calls": [{
                                                                                                                                "calls": [{
                                                                                                                                    "calls": [{
                                                                                                                                        "calls": [{
                                                                                                                                            "calls": [{
                                                                                                                                                "calls": [{
                                                                                                                                                    "calls": [{
                                                                                                                                                        "calls": [{
                                                                                                                                                            "calls": [{
                                                                                                                                                                "calls": [{
                                                                                                                                                                    "calls": [{
                                                                                                                                                                        "calls": [{
                                                                                                                                                                            "calls": [{
                                                                                                                                                                                "calls": [{
                                                                                                                                                                                    "calls": [{
                                                                                                                                                                                        "calls": [],
                                                                                                                                                                                        "finishTime": 88768673,
                                                                                                                                                                                        "methodName": "doFinish",
                                                                                                                                                                                        "startTime": 88614171
                                                                                                                                                                                    }],
                                                                                                                                                                                    "finishTime": 88776058,
                                                                                                                                                                                    "methodName": "xyz",
                                                                                                                                                                                    "startTime": 85372954
                                                                                                                                                                                }],
                                                                                                                                                                                "finishTime": 88782039,
                                                                                                                                                                                "methodName": "def",
                                                                                                                                                                                "startTime": 81161360
                                                                                                                                                                            }],
                                                                                                                                                                            "finishTime": 88787811,
                                                                                                                                                                            "methodName": "abc",
                                                                                                                                                                            "startTime": 81036267
                                                                                                                                                                        }],
                                                                                                                                                                        "finishTime": 88793756,
                                                                                                                                                                        "methodName": "xyz",
                                                                                                                                                                        "startTime": 76803698
                                                                                                                                                                    }],
                                                                                                                                                                    "finishTime": 88798706,
                                                                                                                                                                    "methodName": "abc",
                                                                                                                                                                    "startTime": 72982824
                                                                                                                                                                }],
                                                                                                                                                                "finishTime": 88803393,
                                                                                                                                                                "methodName": "def",
                                                                                                                                                                "startTime": 70801002
                                                                                                                                                            }],
                                                                                                                                                            "finishTime": 88815397,
                                                                                                                                                            "methodName": "abc",
                                                                                                                                                            "startTime": 69585052
                                                                                                                                                        }],
                                                                                                                                                        "finishTime": 88820518,
                                                                                                                                                        "methodName": "def",
                                                                                                                                                        "startTime": 68356854
                                                                                                                                                    }],
                                                                                                                                                    "finishTime": 88825216,
                                                                                                                                                    "methodName": "abc",
                                                                                                                                                    "startTime": 64118780
                                                                                                                                                }],
                                                                                                                                                "finishTime": 88829772,
                                                                                                                                                "methodName": "xyz",
                                                                                                                                                "startTime": 59947135
                                                                                                                                            }],
                                                                                                                                            "finishTime": 88834387,
                                                                                                                                            "methodName": "def",
                                                                                                                                            "startTime": 59801267
                                                                                                                                        }],
                                                                                                                                        "finishTime": 88838921,
                                                                                                                                        "methodName": "abc",
                                                                                                                                        "startTime": 55580112
                                                                                                                                    }],
                                                                                                                                    "finishTime": 88843187,
                                                                                                                                    "methodName": "def",
                                                                                                                                    "startTime": 53323934
                                                                                                                                }],
                                                                                                                                "finishTime": 88847378,
                                                                                                                                "methodName": "abc",
                                                                                                                                "startTime": 51147716
                                                                                                                            }],
                                                                                                                            "finishTime": 88851289,
                                                                                                                            "methodName": "xyz",
                                                                                                                            "startTime": 46566347
                                                                                                                        }],
                                                                                                                        "finishTime": 88855816,
                                                                                                                        "methodName": "abc",
                                                                                                                        "startTime": 43411431
                                                                                                                    }],
                                                                                                                    "finishTime": 88879575,
                                                                                                                    "methodName": "def",
                                                                                                                    "startTime": 43331502
                                                                                                                }],
                                                                                                                "finishTime": 88884747,
                                                                                                                "methodName": "abc",
                                                                                                                "startTime": 41764628
                                                                                                            }],
                                                                                                            "finishTime": 88888888,
                                                                                                            "methodName": "xyz",
                                                                                                            "startTime": 37449816
                                                                                                        }],
                                                                                                        "finishTime": 88892770,
                                                                                                        "methodName": "def",
                                                                                                        "startTime": 36230914
                                                                                                    }],
                                                                                                    "finishTime": 88896398,
                                                                                                    "methodName": "abc",
                                                                                                    "startTime": 33969092
                                                                                                }],
                                                                                                "finishTime": 88899787,
                                                                                                "methodName": "xyz",
                                                                                                "startTime": 30762847
                                                                                            }],
                                                                                            "finishTime": 88903162,
                                                                                            "methodName": "def",
                                                                                            "startTime": 30039862
                                                                                        }],
                                                                                        "finishTime": 88906745,
                                                                                        "methodName": "xyz",
                                                                                        "startTime": 29766216
                                                                                    }],
                                                                                    "finishTime": 88912806,
                                                                                    "methodName": "def",
                                                                                    "startTime": 27375122
                                                                                }],
                                                                                "finishTime": 88918901,
                                                                                "methodName": "abc",
                                                                                "startTime": 24222156
                                                                            }],
                                                                            "finishTime": 88922169,
                                                                            "methodName": "xyz",
                                                                            "startTime": 24087673
                                                                        }],
                                                                        "finishTime": 88926112,
                                                                        "methodName": "abc",
                                                                        "startTime": 19885171
                                                                    }],
                                                                    "finishTime": 88929139,
                                                                    "methodName": "xyz",
                                                                    "startTime": 18736800
                                                                }],
                                                                "finishTime": 88936373,
                                                                "methodName": "def",
                                                                "startTime": 18516576
                                                            }],
                                                            "finishTime": 88940065,
                                                            "methodName": "xyz",
                                                            "startTime": 17363453
                                                        }],
                                                        "finishTime": 88948241,
                                                        "methodName": "def",
                                                        "startTime": 15262109
                                                    }],
                                                    "finishTime": 88951468,
                                                    "methodName": "xyz",
                                                    "startTime": 15212427
                                                }], "finishTime": 88954241, "methodName": "abc", "startTime": 12106597
                                            }], "finishTime": 88956779, "methodName": "xyz", "startTime": 8975438
                                        }], "finishTime": 88959407, "methodName": "def", "startTime": 7820001
                                    }], "finishTime": 88961961, "methodName": "xyz", "startTime": 6686000
                                }], "finishTime": 88964377, "methodName": "def", "startTime": 6575624
                            }], "finishTime": 88966561, "methodName": "abc", "startTime": 2443126
                        }], "finishTime": 88968627, "methodName": "xyz", "startTime": 2389687
                    }], "finishTime": 88970530, "methodName": "abc", "startTime": 1269212
                }], "finishTime": 88972445, "methodName": "start", "startTime": 0
            }], "threadName": "pool-1-thread-2"
        }
    };
    // const threads = {
    //     1670675563: {
    //         "calls": [{
    //             "calls": [{
    //                 "calls": [{
    //                     "calls": [{
    //                         "calls": [],
    //                         "finishTime": 2325824,
    //                         "methodName": "another1",
    //                         "startTime": 2295370
    //                     }, {"calls": [], "finishTime": 2371870, "methodName": "another2", "startTime": 2346489}],
    //                     "finishTime": 2411970,
    //                     "methodName": "other",
    //                     "startTime": 2258811
    //                 }], "finishTime": 2417888, "methodName": "abs", "startTime": 1899230
    //             }, {"calls": [], "finishTime": 2483579, "methodName": "xyz", "startTime": 2432014}],
    //             "finishTime": 2488463,
    //             "methodName": "start",
    //             "startTime": 0
    //         }], "threadName": "main", "startTime": 0, "finishTime": 2488463
    //     }
    // };
    console.log(threads);
    const thread = new Thread(threads["182478697"]);
    thread.createList($("#tree"));

});
