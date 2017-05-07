/**
 * Created by Liudmila Kornilova
 * on 05.05.17
 */

/*  todo: fix popups
 */

class Thread {
    static get layerColors() {
        return ["#5457FF", "#546EFF", "#547DFF", "#5492FF", "#549DFF", "#54ADFF", "#54C1FF"];
    }

    // static layerColors =
    constructor(thread, startOfFirstTread, finishOfLastTread) {
        this.name = thread.threadName;
        this.tree = thread; // tree of calls
        this.depth = this.countDepthRecursively(this.tree, 0, 0);
        this.tree.startTime = 0;
        this.layerHeight = 23;
        this.startOfFirstTread = startOfFirstTread;
        this.finishOfLastTread = finishOfLastTread;
        this.commonDuration = finishOfLastTread - startOfFirstTread;
    }

    countDepthRecursively(subTree, depth, maxDepth) {
        if (subTree.calls.length === 0) {
            return maxDepth;
        }
        depth++;
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        for (let i = 0; i < subTree.calls.length; i++) {
            maxDepth = this.countDepthRecursively(subTree.calls[i], depth, maxDepth);
        }
        return maxDepth;
    }

    /**
     * Create hierarchical <ol> tree in html element
     * @param htmlElement element in which tree will be created
     */
    createList(htmlElement) {
        const section = $("<section></section>").appendTo(htmlElement);
        section.append($("<h2>" + this.name + "</h2>"));
        section.addClass(this.name);
        const div = $("<div></div>").appendTo(section);
        div.addClass("call-tree");
        div.css("height", this.depth * this.layerHeight + 20);
        div.css("left", (this.tree.startThreadTime - this.startOfFirstTread) / this.commonDuration * 100 + "%");
        div.css("width", (this.tree.duration) / this.commonDuration * 100 + "%");
        div.css("opacity", 0.95);
        this.recursivelyCreateList_(div, this.tree, 0, 0);
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
    recursivelyCreateList_(htmlElement, subTree, depth, id) {
        if (subTree.calls.length === 0) {
            return;
        }
        const ol = $("<ol></ol>").appendTo(htmlElement);
        for (let i = 0; i < subTree.calls.length; i++) { // do not use for-in because order is important
            const childCall = subTree.calls[i];
            const newLi = $("<li><p class='method-name'>" + childCall.methodName + "</p></li>").appendTo(ol);
            this.createPopup(newLi, childCall, id);
            const left = (childCall.startTime - subTree.startTime) / subTree.duration;
            newLi.css("left", (left * 100) + "%");
            newLi.css("width", (childCall.duration) / subTree.duration * 100 + "%");
            this.recursivelyCreateList_(newLi, childCall, depth + 1, id + 1);
        }
    }

    createPopup(newLi, childCall, id) {
        const popup = $(Thread.generatePopup(childCall.methodName, childCall.className, childCall.startTime, childCall.duration))
            .appendTo($("." + this.name));
        newLi.bind("mousemove", (e) => {
            newLi.css("background", "#5457FF");
            popup.show().css("top", e.pageY).css("left", e.pageX);
            return false;
        });
        newLi.bind("mouseout", () => {
            newLi.css("background", "");
            popup.hide();
            return false;
        });
    }

    static generatePopup(methodName, className, startTime, duration) {
        return '<div class="detail"><h3>' + className + ".<b>" + methodName + '</b></h3>' +
            '<p>Start time: ' + Math.round(startTime / 10000) / 100 + ' ms</p>' +
            '<p>Duration: ' + Math.round(duration / 10000) / 100 + ' ms</p></div>';
    }
}

function clearDom() {
    $("main section").remove();
}

$(window).on("load", function () {
    const input = document.querySelectorAll('.inputfile')[0];
    const label = input.nextElementSibling;
    const labelVal = label.innerHTML;

    $('#file').on('change', function (e) {
        const files = e.target.files; // FileList object
        const reader = new FileReader();

        // Closure to capture the file information.
        reader.onload = (function (theFile) {
            reader.readAsText(theFile);
            reader.addEventListener("load", function () {
                console.log(reader.result);
                try {
                    const threadsJson = JSON.parse(reader.result);
                    clearDom();
                    processData(threadsJson);
                    changeName(e);
                }
                catch (exception) {
                    alert("Invalid file :(");
                }
            });
        })(files[0]);

        function changeName(e) {
            let fileName = '';
            if (this.files && this.files.length > 1)
                fileName = ( this.getAttribute('data-multiple-caption') || '' ).replace('{count}', this.files.length);
            else
                fileName = e.target.value.split('\\').pop();

            if (fileName)
                label.querySelector('span').innerHTML = fileName;
            else
                label.innerHTML = labelVal;
        }
    });

    function processData(threadsJson) {
        const keys = Object.keys(threadsJson);
        const threads = [];
        for (let i in keys) { // create array with threads
            // console.log(value);
            threads.push(threadsJson[keys[i]]);
        }
        const startTimes = [];
        const finishTimes = [];
        for (let i in threads) {
            startTimes.push(threads[i].startThreadTime);
            finishTimes.push(threads[i].startThreadTime + threads[i].duration);
        }
        const startOfFirstThread = getMinOfArray(startTimes);
        const finishOfFirstThread = getMaxOfArray(finishTimes);
        for (let i in threads) {
            new Thread(threads[i], startOfFirstThread, finishOfFirstThread).createList($('main'));
        }
        function getMaxOfArray(numArray) {
            return Math.max.apply(null, numArray);
        }

        function getMinOfArray(numArray) {
            return Math.min.apply(null, numArray);
        }
    }
});
