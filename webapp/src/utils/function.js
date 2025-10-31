// Loading point when await
function awaitFront() {
  const mainAwait = document.querySelector('main');

  mainAwait.innerHTML = `
        <div class="await d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-circle"></i>
            <i class="fa-solid fa-circle"></i>
            <i class="fa-solid fa-circle"></i>
        </div>
    `;
}

// Update NavBar selected
const showNavStyle = (id) => {
  const allNav = document.querySelectorAll(".nav-btn");

  allNav.forEach((nav) => {
    const newNav = nav;
    newNav.style.boxShadow = "0px 0px 0px";
    newNav.style.fontWeight = "normal";
  });

  const selectedNav = document.getElementById(id);
  if (id === "home") {
    selectedNav.style.boxShadow = "8px 8px 0px var(--accent-color)";
    // selectedNav.style.fontWeight = "bold";
  } else {
    selectedNav.style.boxShadow = "8px 8px 0px var(--ma-couleur)";
    selectedNav.style.fontWeight = "bold";
  }
};

export {
  showNavStyle,
  awaitFront
};