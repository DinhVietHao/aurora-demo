// View by and Collapse
const moreBtn = document.querySelector('#more');
const gradient = document.querySelector('.gradient');


moreBtn.addEventListener('click', () => {
    if (moreBtn.innerText === 'Xem thêm') {
        moreBtn.innerText = 'Thu gọn'
        gradient.style.display = 'none'

    } else {
        moreBtn.innerText = 'Xem thêm'
        gradient.style.display = 'block'
    }

})
// End View by and Collapse

// handle click thumbnail image → change main image
const thumbnails = document.querySelectorAll('.thumbnail')
const mainImage = document.querySelector('#mainImage')

thumbnails.forEach(img => {
    img.addEventListener('click', () => {
        thumbnails.forEach(active => active.classList.remove('active'))
        mainImage.src = img.src;
        img.classList.add('active')
    })
})
//END handle click thumbnail image → change main image
