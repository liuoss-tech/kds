import request from '@/utils/request';

export function getRecipe(params) {
  return request({
    url: '/api/busi/recipe',
    method: 'get',
    params
  });
}

export function saveRecipe(data) {
  return request({
    url: '/api/busi/recipe',
    method: 'post',
    data
  });
}

export function getParentRecipe(params) {
  return request({
    url: '/api/busi/recipe/parent',
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