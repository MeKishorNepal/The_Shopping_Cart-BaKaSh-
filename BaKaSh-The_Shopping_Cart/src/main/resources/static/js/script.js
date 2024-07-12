
  document.addEventListener("DOMContentLoaded", function () {
         // Array of background images and corresponding content
         const slides = [
            { image: 'img/T-shirt.jpg', title: 'Visit Our', subTitle: 'Exclusive Collection', description: 'Check out our latest exclusive collection of stylish shoes.' },
            { image: 'img/Furniture.jpg', title: 'Explore Our', subTitle: 'New Generation', description: 'Explore the latest trends in men\'s shoes with our new arrivals.' },
            { image: 'img/hoddi.jpg', title: 'Shop Now', subTitle: 'Limited Edition', description: 'Grab your pair of limited edition shoes before they run out of stock.' }
         ];

         let currentIndex = 0;

         // Function to update background and content
         function updateBackgroundAndContent() {
            // Update background image
            document.getElementById('slideMain').style.backgroundImage = `url('${slides[currentIndex].image}')`;

            // Update content in the new-arrival div
            document.querySelector('.new-arrival h2').textContent = slides[currentIndex].title;
            document.querySelector('.new-arrival p:first-of-type').textContent = slides[currentIndex].subTitle;
            document.querySelector('.new-arrival p:last-of-type').textContent = slides[currentIndex].description;

            // Increment index for the next background image
            currentIndex = (currentIndex + 1) % slides.length;
         }

         // Set an interval to update background and content every 5 seconds (5000 milliseconds)
         setInterval(updateBackgroundAndContent, 3000);

         // Initial update to set the first background image and content
         updateBackgroundAndContent();
      });