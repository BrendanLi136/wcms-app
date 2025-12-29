const app = getApp();
Page({
    data: {
        type: 'history',
        list: []
    },
    onLoad(opt) {
        this.setData({ type: opt.type });
        this.fetchData();
    },
    fetchData() {
        const user = wx.getStorageSync('user');
        if (!user) return;

        let url = '';
        if (this.data.type === 'history') {
            url = app.globalData.baseUrl + '/history?patientId=' + user.id;
        } else if (this.data.type === 'reminder') {
            url = app.globalData.baseUrl + '/reminders?patientId=' + user.id;
        }

        if (!url) return;

        wx.showLoading({ title: '加载中...' });
        wx.request({
            url: url,
            method: 'GET',
            success: (res) => {
                wx.hideLoading();
                if (res.data.code === 200) {
                    this.setData({ list: res.data.data });
                } else {
                    wx.showToast({ title: '获取数据失败', icon: 'none' });
                }
            },
            fail: () => {
                wx.hideLoading();
                wx.showToast({ title: '网络错误', icon: 'none' });
            }
        });
    }
});
