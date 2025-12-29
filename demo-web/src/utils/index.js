export const show = function () {
    (function fairyDustCursor() {
        var possibleColors = ["#D61C59", "#E7D84B", "#1B8798"]
        var width = window.innerWidth;
        var height = window.innerHeight;
        var cursor = { x: width / 2, y: width / 2 };
        var particles = [];
        function init() {
            bindEvents();
            loop();
        }
        function bindEvents() {
            document.addEventListener('mousemove', onMouseMove);
            window.addEventListener('resize', onWindowResize);
        }
        function onWindowResize(e) {
            width = window.innerWidth;
            height = window.innerHeight;
        }
        function onMouseMove(e) {
            cursor.x = e.clientX;
            cursor.y = e.clientY;
            addParticle(cursor.x, cursor.y, possibleColors[Math.floor(Math.random() * possibleColors.length)]);
        }
        function addParticle(x, y, color) {
            var particle = new Particle();
            particle.init(x, y, color);
            particles.push(particle);
        }
        function updateParticles() {
            for (var i = 0; i < particles.length; i++) {
                particles[i].update();
            }
            for (var i = particles.length - 1; i >= 0; i--) {
                if (particles[i].lifeSpan < 0) {
                    particles[i].die();
                    particles.splice(i, 1);
                }
            }
        }
        function loop() {
            requestAnimationFrame(loop);
            updateParticles();
        }
        function Particle() {
            this.character = "*";
            this.lifeSpan = 120; //ms
            this.initialStyles = {
                "position": "fixed",
                "display": "inline-block",
                "top": "0px",
                "left": "0px",
                "pointerEvents": "none",
                "touch-action": "none",
                "z-index": "10000000",
                "fontSize": "25px",
                "will-change": "transform"
            };
            // Init, and set properties
            this.init = function (x, y, color) {
                this.velocity = {
                    x: (Math.random() < 0.5 ? -1 : 1) * (Math.random() / 2),
                    y: 1
                };
                this.position = { x: x + 10, y: y + 10 };
                this.initialStyles.color = color;
                this.element = document.createElement('span');
                this.element.innerHTML = this.character;
                applyProperties(this.element, this.initialStyles);
                this.update();
                document.getElementById('app').appendChild(this.element);
            };
            this.update = function () {
                this.position.x += this.velocity.x;
                this.position.y += this.velocity.y;
                this.lifeSpan--;
                this.element.style.transform = "translate3d(" + this.position.x + "px," + this.position.y + "px, 0) scale(" + (this.lifeSpan / 120) + ")";
            }
            this.die = function () {
                this.element.parentNode.removeChild(this.element);
            }
        }
        function applyProperties(target, properties) {
            for (var key in properties) {
                target.style[key] = properties[key];
            }
        }
        if (!('ontouchstart' in window || navigator.msMaxTouchPoints)) init();
    })();
}

/* 判断各个浏览器版本号 */
export const browserMatch = function matchVesion() {
    let userAgent = navigator.userAgent;
    let rMsie = /(msie\s|trident.*rv:)([\w.]+)/;
    let rEdge = /(edg)\/([\w.]+)/;
    let rFirefox = /(firefox)\/([\w.]+)/;
    let rOpera = /(opera).+version\/([\w.]+)/;
    let rChrome = /(chrome)\/([\w.]+)/;
    let rSafari = /version\/([\w.]+).*(safari)/;
    let ua = userAgent.toLowerCase();
    var match = rMsie.exec(ua);
    if (match !== null) {
        return {
            browser: "IE",
            version: match[2] || "0"
        };
    }
    var rEmatch = rEdge.exec(ua);
    if (rEmatch !== null) {
        return {
            browser: 'Edge',
            version: rEmatch[2] || "0"
        };
    }
    var rFmatch = rFirefox.exec(ua);
    if (rFmatch !== null) {
        return {
            browser: rFmatch[1] || "",
            version: rFmatch[2] || "0"
        };
    }
    var rOmatch = rOpera.exec(ua);
    if (rOmatch !== null) {
        return {
            browser: rOmatch[1] || "",
            version: rOmatch[2] || "0"
        };
    }
    var rCmatch = rChrome.exec(ua);
    if (rCmatch !== null) {
        return {
            browser: rCmatch[1] || "",
            version: rCmatch[2] || "0"
        };
    }
    var rSmatch = rSafari.exec(ua);
    if (rSmatch !== null) {
        return {
            browser: rSmatch[2] || "",
            version: rSmatch[1] || "0"
        };
    }
    if (match !== null) {
        return {
            browser: "",
            version: "0"
        };
    }
}


