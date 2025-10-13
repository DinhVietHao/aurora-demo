const goToOrder = document.getElementById("go-to-order");
const goToProfile = document.getElementById("go-to-profile");

if (goToOrder) {
  goToOrder.addEventListener("click", (e) => {
    e.preventDefault();
    var orderTab = new bootstrap.Tab(document.getElementById("order-tab"));
    orderTab.show();
    localStorage.setItem("activeTab", "order-tab");
  });
}

if (goToProfile) {
  goToProfile.addEventListener("click", (e) => {
    e.preventDefault();
    var profileTab = new bootstrap.Tab(document.getElementById("profile-tab"));
    profileTab.show();
    localStorage.setItem("activeTab", "profile-tab");
  });
}

const lastTab = localStorage.getItem("activeTab");
if (lastTab) {
  const tabElement = document.getElementById(lastTab);
  if (tabElement) {
    const restoredTab = new bootstrap.Tab(tabElement);
    restoredTab.show();
  }
}
