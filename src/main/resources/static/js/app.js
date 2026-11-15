(function () {
  "use strict";

  const FIELDS = [
    "hostname",
    "ipAddress",
    "instanceId",
    "containerId",
    "currentTimeUtc",
    "currentTimeLocal",
    "environment",
    "appVersion",
    "gitSha",
  ];

  const BADGE_FIELDS = {
    container: "container",
    kubernetes: "kubernetes",
  };

  function updateBadge(id, value) {
    const el = document.getElementById(id);
    if (!el) return;
    const isTrue = value === true || value === "true";
    el.textContent = isTrue ? "YES" : "NO";
    el.className = isTrue ? "badge badge-yes" : "badge badge-no";
  }

  function refresh() {
    fetch("/api/info")
      .then(function (r) {
        return r.json();
      })
      .then(function (data) {
        FIELDS.forEach(function (f) {
          var el = document.getElementById(f);
          if (el && data[f] !== undefined) {
            el.textContent = data[f];
          }
        });
        Object.keys(BADGE_FIELDS).forEach(function (key) {
          updateBadge(key, data[key]);
        });
        document.getElementById("lastUpdated").textContent =
          new Date().toLocaleTimeString();
      })
      .catch(function () {
        document.getElementById("lastUpdated").textContent = "connection lost";
      });
  }

  setInterval(refresh, 2000);
  refresh();
})();
