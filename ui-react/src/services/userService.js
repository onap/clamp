const userService = {
    login
};

function login(username, password) {
    let options = {
           method: 'GET'
         };
    if (username && password) {
      options = {
             method: 'GET',
             credentials: 'include',
             headers: {
                'Authorization': "Basic " + new Buffer(username + ":" + password).toString("base64")
             }
           };
    }

    return fetch(`/restservices/clds/v1/user/getUser`, options)
      .then(response => handleResponse(response))
      .then(function(data) {
          localStorage.setItem('user', data);
          console.log(data);
});
}

function handleResponse(response) {
     if (!response.ok || response.redirected === true) {
          if (response.status === 401 || response.status === 500 || response.redirected === true) {
              if (localStorage.getItem('tryBasicAuth')) {
                // login failed, go to invalud login page
                localStorage.removeItem('user');
              } else {
                // try to login with username and password
                localStorage.setItem('tryBasicAuth', true);
              }
          }
          const error = response.statusText;
          return Promise.reject(error);
      }
    return response.text();
}
export default userService;
