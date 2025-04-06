// src/main/resources/static/js/script.js
document.addEventListener('DOMContentLoaded', function() {
    setupEndpointLinks();
});

function setupEndpointLinks() {
    const endpoints = [
        { name: 'Register Customer', path: '/customers/register', method: 'POST', body: { username: "testUser", password: "testPass" } },
        { name: 'Get Customer By ID', path: '/customers/{id}', method: 'GET' },
        { name: 'Authenticate Customer', path: '/customers/authenticate', method: 'POST', body: { username: "testUser", password: "testPass" } },
        { name: 'Add Media', path: '/media/add', method: 'POST', body: { name: "New Movie", synopsis: "Synopsis here", isMovie: true, smallPosterPath: "path/to/small/poster", largePosterPath: "path/to/large/poster", rentPrice: 2.99, purchasePrice: 9.99, price: 9.99, isFeatured: true } },
        { name: 'All Movies', path: '/media/movies', method: 'GET' },
        { name: 'All TV Shows', path: '/media/tvshows', method: 'GET' },
        { name: 'Search Media', path: '/media/search?title=someTitle', method: 'GET' },
        { name: 'Featured Media', path: '/media/featured', method: 'GET' },
        { name: 'Get Media By ID', path: '/media/{id}', method: 'GET' },
        // Add more endpoints as needed
    ];

    const list = document.getElementById('endpoint-list');
    endpoints.forEach((endpoint, index) => {
        let listItem = document.createElement('li');
        let link = document.createElement('a');
        link.textContent = endpoint.name;
        link.href = '#';
        link.onclick = function() {
            if (endpoint.method === 'POST') {
                fetchAndDisplayData(endpoint.path, endpoint.method, endpoint.body);
            } else {
                // Replace {id} with an actual ID or remove it from path if not applicable
                let path = endpoint.path.replace("{id}", "someId");
                fetchAndDisplayData(path, endpoint.method);
            }
            return false;
        };
        listItem.appendChild(link);
        list.appendChild(listItem);
    });
}

function fetchAndDisplayData(path, method, body = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    fetch(path, options)
        .then(response => response.json())
        .then(data => displayData(data))
        .catch(error => {
            console.error('Error fetching data:', error);
            displayError('Failed to load data.');
        });
}

function displayData(data) {
    const displayDiv = document.getElementById('data-display');
    displayDiv.innerHTML = ''; // Clear previous content
    const pre = document.createElement('pre');
    pre.textContent = JSON.stringify(data, null, 2); // Pretty print JSON data
    displayDiv.appendChild(pre);
}

function displayError(message) {
    const displayDiv = document.getElementById('data-display');
    displayDiv.innerHTML = `<p>${message}</p>`;
}
