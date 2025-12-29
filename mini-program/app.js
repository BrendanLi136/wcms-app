// app.js
App({
    onLaunch() {
        this.checkLoginStatus();
    },
    checkLoginStatus() {
        const user = wx.getStorageSync('user');
        if (user) {
            wx.checkSession({
                success: () => {
                    // Session is valid
                    this.globalData.userInfo = user;
                },
                fail: () => {
                    // Session expired
                    wx.removeStorageSync('user');
                    this.globalData.userInfo = null;
                }
            });
        }
    },
    globalData: {
        userInfo: null,
        baseUrl: 'http://localhost:8080/api/app'
    }
})
