import request from '@/utils/request';

export function getParentLifeRecord(params) {
  return request({
    url: '/api/busi/life-record/parent/page',
    method: 'get',
    params
  });
}

export function getParentChildren() {
  return request({
    url: '/api/busi/student/parent-children',
    method: 'get'
  });
}
