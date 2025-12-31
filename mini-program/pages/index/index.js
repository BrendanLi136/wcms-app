const app = getApp();

Page({
    data: {
        images: [], // Local temp paths
        genderIndex: null, // 0: Male, 1: Female
        defaultName: '',
        defaultAge: '',
        defaultMedicalRecordNo: '',
        isAgreed: false
    },
    onShow() {
        // Auto-fill logic
        const last = wx.getStorageSync('last_patient_info');
        if (last) {
            this.setData({
                defaultName: last.name || '',
                defaultAge: last.age || '',
                defaultMedicalRecordNo: last.medicalRecordNo || '',
                genderIndex: last.genderIndex !== undefined ? last.genderIndex : null
            });
        }

        // Check Auth
        const user = wx.getStorageSync('user');
        if (!user) {
            wx.showModal({
                title: '提示',
                content: '您尚未登录，请先授权登录',
                showCancel: false,
                confirmText: '去授权',
                success: (res) => {
                    if (res.confirm) {
                        wx.navigateTo({ url: '/pages/login/login' });
                    }
                }
            });
        }
    },
    chooseImage() {
        wx.chooseImage({
            count: 5 - this.data.images.length,
            success: (res) => {
                this.setData({
                    images: this.data.images.concat(res.tempFilePaths)
                });
            }
        });
    },
    deleteImage(e) {
        const index = e.currentTarget.dataset.index;
        const images = this.data.images;
        images.splice(index, 1);
        this.setData({
            images: images
        });
    },
    onGenderChange(e) {
        this.setData({
            genderIndex: e.detail.value
        });
    },
    viewConsent() {
        wx.showModal({
            title: '知情同意书',
            content: '此处为知情同意书内容示例...\n\n1. 数据仅用于医疗分析。\n2. 我们会保护您的隐私。',
            showCancel: false
        });
    },
    // Helper to upload a single file as a Promise
    uploadFilePromise(filePath, patientId) {
        return new Promise((resolve, reject) => {
            wx.uploadFile({
                url: app.globalData.baseUrl + '/upload',
                filePath: filePath,
                name: 'file',
                formData: {
                    'patientId': patientId
                },
                success: (res) => {
                    try {
                        const data = JSON.parse(res.data);
                        if (data.code === 200) {
                            resolve(data.data); // Return the URL
                        } else {
                            reject(data.message);
                        }
                    } catch (e) {
                        reject("Parse Error");
                    }
                },
                fail: (err) => {
                    reject(err.errMsg);
                }
            });
        });
    },
    onConsentChange(e) {
        this.setData({
            isAgreed: e.detail.value.length > 0
        });
    },
    async submitForm(e) {
        const val = e.detail.value;
        if (!val.name) {
            wx.showToast({ title: '请输入患者姓名', icon: 'none' });
            return;
        }
        if (this.data.genderIndex === null) {
            wx.showToast({ title: '请选择性别', icon: 'none' });
            return;
        }
        if (!val.age) {
            wx.showToast({ title: '请输入年龄', icon: 'none' });
            return;
        }
        if (this.data.images.length === 0) {
            wx.showToast({ title: '请先选择图片', icon: 'none' });
            return;
        }
        if (!this.data.isAgreed) {
            wx.showToast({ title: '请先同意知情同意书', icon: 'none' });
            return;
        }

        const user = wx.getStorageSync('user');
        if (!user) return wx.navigateTo({ url: '/pages/login/login' });

        // Show loading
        wx.showLoading({ title: '上传分析中...', mask: true });

        try {
            // 1. Upload all images first
            const uploadPromises = this.data.images.map(path => this.uploadFilePromise(path, user.id));
            const uploadedUrls = await Promise.all(uploadPromises);

            // 2. Submit Data
            const req = {
                patientId: user.id, // Current User's ID as the submitter (or patient themselves)
                name: val.name,
                age: parseInt(val.age),
                gender: parseInt(this.data.genderIndex) + 1, // 0->1(Male), 1->2(Female)
                history: val.history || '',
                imageUrls: uploadedUrls,
                medicalRecordNo: val.medicalRecordNo || ''
            };

            // Save for next time
            wx.setStorageSync('last_patient_info', {
                name: val.name,
                age: val.age,
                medicalRecordNo: val.medicalRecordNo,
                genderIndex: this.data.genderIndex
            });

            // 3. Call Submit API
            wx.request({
                url: app.globalData.baseUrl + '/analysis/submit',
                method: 'POST',
                data: req,
                success: (res) => {
                    wx.hideLoading();
                    if (res.data.code === 200) {
                        wx.showToast({ title: '提交成功' });
                        // Reset
                        this.setData({ images: [], genderIndex: null, isAgreed: false });
                        // Go to mine or list
                        setTimeout(() => {
                            wx.switchTab({ url: '/pages/mine/mine' });
                        }, 1500);
                    } else {
                        wx.showToast({ title: '提交失败: ' + res.data.message, icon: 'none' });
                    }
                },
                fail: () => {
                    wx.hideLoading();
                    wx.showToast({ title: '网络请求失败', icon: 'none' });
                }
            });

        } catch (error) {
            wx.hideLoading();
            wx.showToast({ title: '图片上传失败', icon: 'none' });
            console.error(error);
        }
    }
});
