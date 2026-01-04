// pages/mine/mine.js
Page({
    data: {
        userInfo: {},
        showModal: false,
        formData: {
            phone: '',
        }
    },
    onShow() {
        const u = wx.getStorageSync('user');
        if (u) {
            this.setData({
                userInfo: u,
                formData: {
                    phone: u.phone || '',
                }
            });
        }
    },
    goHistory() {
        wx.navigateTo({ url: '/pages/history/history' });
    },
    goReminders() {
        wx.navigateTo({ url: '/pages/reminders/reminders' });
    },
    toggleModal() {
        this.setData({ showModal: !this.data.showModal });
    },
    inputChange(e) {
        const field = e.currentTarget.dataset.field;
        const value = e.detail.value;
        this.setData({
            [`formData.${field}`]: value
        });
    },
    saveProfile() {
        const app = getApp();
        const { formData, userInfo } = this.data;

        if (!formData.phone) {
            wx.showToast({ title: '手机号必填', icon: 'none' });
            return;
        }

        wx.showLoading({ title: '保存中...' });
        wx.request({
            url: app.globalData.baseUrl + '/api/app/updateProfile',
            method: 'POST',
            data: {
                userId: userInfo.id,
                ...formData
            },
            success: (res) => {
                wx.hideLoading();
                if (res.data.code === 200) {
                    wx.setStorageSync('user', res.data.data);
                    this.setData({
                        userInfo: res.data.data,
                        showModal: false
                    });
                    wx.showToast({ title: '绑定成功' });
                } else {
                    wx.showToast({ title: '失败: ' + res.data.message, icon: 'none' });
                }
            },
            fail: () => {
                wx.hideLoading();
                wx.showToast({ title: '网络请求失败', icon: 'none' });
            }
        })
    },
    handleLogout() {
        wx.removeStorageSync('user');
        wx.reLaunch({ url: '/pages/login/login' });
    }
});
