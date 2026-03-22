import request from './request.js';

export const getLifeRecordPage = (params) => {
  return request({
    url: '/api/busi/life-record/page',
    method: 'GET',
    params
  });
};

export const saveLifeRecord = (data) => {
  return request({
    url: '/api/busi/life-record/save',
    method: 'POST',
    data
  });
};

export const batchSaveLifeRecord = (data) => {
  return request({
    url: '/api/busi/life-record/batch-save',
    method: 'POST',
    data
  });
};

export default {
  getLifeRecordPage,
  saveLifeRecord,
  batchSaveLifeRecord
};
