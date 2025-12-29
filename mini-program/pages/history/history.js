const app = getApp();
Page({
    data: {
        list: []
    },
    onLoad() {
        this.fetchData();
    },
    fetchData() {
        const user = wx.getStorageSync('user');
        if (!user) return;

        wx.showLoading({ title: '加载中...' });
        wx.request({
            url: app.globalData.baseUrl + '/history?patientId=' + user.id,
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
    },
    viewDetail(e) {
        const id = e.currentTarget.dataset.id;
        // Future: Navigate to detail page
        wx.showModal({
            title: '详情',
            content: '查看记录详情 ID: ' + id,
            showCancel: false
        });
    }
});
