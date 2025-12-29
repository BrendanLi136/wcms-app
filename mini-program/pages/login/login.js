// pages/login/login.js
const app = getApp();

Page({
    data: {
        hasPhone: false,
        tempUserId: null
    },
    onShow() {
        // If already logged in, redirect
        const user = wx.getStorageSync('user');
        if (user) {
            wx.checkSession({
                success: () => {
                    wx.switchTab({ url: '/pages/index/index' });
                },
                fail: () => {
                    wx.removeStorageSync('user');
                }
            })
        }
    },
    handleLogin() {
        const user = wx.getStorageSync('user');
        if (user) {
            wx.checkSession({
                success: () => {
                    wx.switchTab({ url: '/pages/index/index' });
                    return;
                },
                fail: () => {
                    wx.removeStorageSync('user');
                    this.doLogin();
                }
            });
        } else {
            this.doLogin();
        }
    },
    doLogin() {
        wx.login({
            success: (res) => {
                if (res.code) {
                    wx.request({
                        url: app.globalData.baseUrl + '/login',
                        method: 'POST',
                        data: { code: res.code },
                        success: (resp) => {
                            if (resp.data.code === 200) {
                                const user = resp.data.data;
                                wx.setStorageSync('user', user);
                                wx.switchTab({ url: '/pages/index/index' });
                            } else {
                                wx.showToast({ title: '登录失败' });
                            }
                        }
                    });
                }
            }
        });
    },
    getPhoneNumber(e) {
        console.log('getPhoneNumber detail:', e.detail);
        if (e.detail.code) {
            wx.request({
                url: app.globalData.baseUrl + '/updatePhone',
                method: 'POST',
                data: {
                    userId: this.data.tempUserId,
                    code: e.detail.code,
                    encryptedData: e.detail.encryptedData,
                    iv: e.detail.iv
                },
                success: (res) => {
                    if (res.data.code === 200) {
                        wx.setStorageSync('user', res.data.data);
                        wx.switchTab({ url: '/pages/index/index' });
                    } else {
                        wx.showToast({ title: '获取手机号失败: ' + res.data.message, icon: 'none' });
                    }
                }
            })
        } else {
            wx.showToast({ title: '您拒绝了授权: ' + e.detail.errMsg, icon: 'none' });
        }
    },
    // Dev Helper: Skip phone auth
    skipAuth() {
        wx.showToast({ title: '开发模式：跳过手机授权', icon: 'none' });
        setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
    }
});
