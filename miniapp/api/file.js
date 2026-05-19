const BASE_URL = 'http://localhost:8080'

export const uploadImage = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.uploadFile({
      url: BASE_URL + '/api/app/file/upload',
      filePath,
      name: 'file',
      header: { 'Authorization': token ? `Bearer ${token}` : '' },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data.url)
        } else {
          reject(data)
        }
      },
      fail: reject
    })
  })
}
