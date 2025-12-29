// pages/mine/mine.js
Page({
    data: {
        userInfo: {}
    },
    onShow() {
        const u = wx.getStorageSync('user');
        if (u) this.setData({ userInfo: u });
    },
    goHistory() {
        wx.navigateTo({ url: '/pages/history/history' });
    },
    goReminders() {
        wx.navigateTo({ url: '/pages/reminders/reminders' });
    },
    getPhoneNumber(e) {
        const app = getApp();
        if (e.detail.code) {
            wx.showLoading({ title: '绑定中...' });
            wx.request({
                url: app.globalData.baseUrl + '/updatePhone',
                method: 'POST',
                data: {
                    userId: this.data.userInfo.id,
                    code: e.detail.code,
                    encryptedData: e.detail.encryptedData,
                    iv: e.detail.iv
                },
                success: (res) => {
                    wx.hideLoading();
                    if (res.data.code === 200) {
                        wx.setStorageSync('user', res.data.data);
                        this.setData({ userInfo: res.data.data });
                        wx.showToast({ title: '绑定成功' });
                    } else {
                        wx.showToast({ title: '失败: ' + res.data.message, icon: 'none' });
                    }
                },
                fail: () => wx.hideLoading()
            })
        }
    },
    handleLogout() {
        wx.removeStorageSync('user');
        wx.reLaunch({ url: '/pages/login/login' });
    }
});
