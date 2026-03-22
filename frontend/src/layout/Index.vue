<template>
  <el-container class="layout-container">
    <el-aside width="260px" class="sidebar">
      <div class="logo-area">
        <div class="logo-content">
          <div class="logo-icon-group">
            <span class="logo-house">🏠</span>
            <span class="logo-flower">🌸</span>
          </div>
          <div class="logo-glow"></div>
        </div>
        <span class="logo-text">KDS 家校通</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="transparent"
        text-color="#5D4E4E"
        active-text-color="#FF6B6B"
        router
        @select="handleMenuSelect"
      >
        <template v-for="(item, index) in dynamicMenus">
          <el-submenu 
            v-if="item.children && item.children.length > 0"
            :key="item.id"
            :index="`menu-${item.id}`"
            class="menu-submenu-animated"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <template #title>
              <i :class="item.icon || 'el-icon-menu'" class="menu-icon"></i>
              <span>{{ item.permName }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.id"
              :index="child.routePath"
              class="menu-item-animated"
              :style="{ animationDelay: `${index * 0.05 + 0.02}s` }"
            >
              <i :class="child.icon || 'el-icon-s-check'" class="menu-icon"></i>
              <span>{{ child.permName }}</span>
            </el-menu-item>
          </el-submenu>
          <el-menu-item 
            v-else
            :key="`menu-item-${item.id}`"
            :index="item.routePath"
            class="menu-item-animated"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <i :class="item.icon || 'el-icon-menu'" class="menu-icon"></i>
            <span>{{ item.permName }}</span>
          </el-menu-item>
        </template>
      </el-menu>

      <div class="sidebar-footer">
        <div class="footer-decoration">
          <span class="deco-flower">❀</span>
          <span class="deco-line"></span>
          <span class="deco-flower">✿</span>
        </div>
      </div>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="breadcrumb">
            <span class="breadcrumb-item">🏠 {{ currentPageTitle }}</span>
          </div>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-dropdown">
              <el-avatar 
                size="medium" 
                :style="{ backgroundColor: avatarColor }"
                class="avatar-animated"
              >
                {{ realName?.charAt(0) || '用' }}
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ realName }}</span>
                <span class="user-role">{{ roleName }}</span>
              </div>
              <i class="el-icon-arrow-down dropdown-arrow"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="dropdown-menu-animated">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <div class="content-bg">
          <div class="cloud cloud-1">
            <div class="cloud-body"></div>
          </div>
          <div class="cloud cloud-2">
            <div class="cloud-body"></div>
          </div>
          <div class="star" v-for="i in 6" :key="i" :style="{ '--i': i }">⭐</div>
        </div>
        <router-view v-slot="{ Component }">
          <transition 
            name="page" 
            mode="out-in"
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <el-backtop :right="40" :bottom="40" class="back-top-animated">
      <span>⬆</span>
    </el-backtop>
  </el-container>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'Layout',
  data() {
    return {
      realName: localStorage.getItem('kds_realName') || '用户',
      roleName: '',
      activeMenu: '',
      
      dynamicMenus: []
    };
  },
  computed: {

    currentPageTitle() {
      const findMenu = (menus) => {
        for (const menu of menus) {
          if (menu.routePath === this.$route.path) {
            return menu;
          }
          if (menu.children && menu.children.length > 0) {
            const found = findMenu(menu.children);
            if (found) return found;
          }
        }
        return null;
      };
      const currentMenu = findMenu(this.dynamicMenus);
      return currentMenu ? currentMenu.permName : '首页';
    },

    avatarColor() {
      const colors = ['#FFB5B5', '#FFD93D', '#6BCB77', '#4D96FF', '#C9B1FF', '#FF9F45'];
      const index = this.realName?.charCodeAt(0) % colors.length || 0;
      return colors[index];
    }
  },
  created() {
    this.fetchMyMenus();
    this.fetchUserInfo();
  },
  methods: {
    async fetchUserInfo() {
      try {
        const res = await request.get('/api/auth/info');
        if (res.data) {
          this.realName = res.data.realName;
          this.roleName = res.data.roleName;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.roleName = '未知角色';
      }
    },
    fetchMyMenus() {
      request.get('/api/system/permission/my-menus').then(res => {
        const flatData = res.data || [];
        this.dynamicMenus = this.buildMenuTree(flatData);
      });
    },
    buildMenuTree(flatData) {
      const allNodes = flatData.map(item => ({ ...item, children: [] }));
      return allNodes
        .filter(node => node.parentId === 0 || node.parentId === null)
        .map(root => {
          root.children = allNodes.filter(node => node.parentId === root.id);
          return root;
        });
    },
    handleMenuSelect(index) {
      this.activeMenu = index;
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'profile') {
        this.$message.info('个人中心开发中...');
      }
    },
    logout() {
      const loading = this.$loading({
        lock: true,
        text: '正在退出...',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.9)'
      });
      
      request.post('/api/auth/logout').then(() => {
        localStorage.clear();
        loading.close();
        this.$message.success('已安全退出系统');
        this.$router.push('/login');
      }).catch(() => {
        localStorage.clear();
        loading.close();
        this.$router.push('/login');
      });
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.layout-container {
  height: 100vh;
  width: 100%;
  font-family: 'Noto Sans SC', 'ZCOOL KuaiLe', sans-serif;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 200px !important;
  min-width: 200px !important;
  background: linear-gradient(180deg, #FFF9F5 0%, #FFF0F0 50%, #FFE8E8 100%);
  box-shadow: 4px 0 24px rgba(255, 182, 193, 0.15);
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.sidebar .el-menu {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 182, 193, 0.5) transparent;
}

.sidebar .el-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar .el-menu::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar .el-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 182, 193, 0.5);
  border-radius: 3px;
}

.sidebar .el-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 182, 193, 0.7);
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF6B6B, #FF8E53, #FFD93D, #6BCB77, #4D96FF);
  background-size: 200% 100%;
  animation: gradientFlow 3s linear infinite;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

.logo-area {
  height: 90px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, #FFB5B5 0%, #FFD4D4 50%, #FFCCCC 100%);
  position: relative;
  overflow: hidden;
}

.logo-area::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.15'%3E%3Cpath d='M20 20.5V18H0v-2h20v-2H0v-2h20v-2H0V8h20V6H0V4h20V2H0V0h22v20h2V0h2v20h2V0h2v20h2V0h2v20h2v2.5l-2 2z'/%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.8;
}

.logo-content {
  position: relative;
  z-index: 1;
}

.logo-icon-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.logo-house {
  font-size: 28px;
  animation: houseBounce 2s ease-in-out infinite;
}

@keyframes houseBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

.logo-flower {
  font-size: 24px;
  animation: flowerSpin 3s ease-in-out infinite;
}

@keyframes flowerSpin {
  0%, 100% { transform: rotate(-5deg) scale(1); }
  50% { transform: rotate(5deg) scale(1.1); }
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 30px;
  background: radial-gradient(ellipse, rgba(255, 255, 255, 0.6) 0%, transparent 70%);
}

.logo-text {
  position: relative;
  z-index: 1;
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: #5D4E4E;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.el-menu-vertical {
  border-right: none;
  padding: 20px 0;
}

.menu-item-animated {
  margin: 6px 8px 6px 16px;
  border-radius: 16px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: slideIn 0.4s ease-out forwards;
  opacity: 0;
  transform: translateX(-20px);
  background: rgba(255, 255, 255, 0.7);
}

@keyframes slideIn {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.menu-item-animated:hover {
  margin-right: 8px;
  transform: translateX(8px) scale(1.02);
  background: rgba(255, 230, 230, 0.95);
  box-shadow: 0 8px 20px rgba(255, 107, 107, 0.12);
}

.menu-item-animated.is-active {
  background: linear-gradient(135deg, #FFB5B5 0%, #FFD4D4 100%) !important;
  color: #FF6B6B !important;
  box-shadow: 0 8px 20px rgba(255, 107, 107, 0.2);
  margin-right: 8px;
}

.menu-item-animated.is-active::before {
  content: '✨';
  position: absolute;
  left: -8px;
  font-size: 12px;
  animation: sparkle 1s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}

.menu-item-animated.is-active .menu-icon {
  transform: scale(1.15);
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: transform 0.3s ease;
}

.menu-title {
  font-weight: 500;
  letter-spacing: 1px;
}

.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-decoration {
  display: flex;
  align-items: center;
  gap: 12px;
}

.deco-flower {
  font-size: 16px;
  animation: flowerFloat 3s ease-in-out infinite;
}

.deco-flower:nth-child(3) {
  animation-delay: -1.5s;
}

@keyframes flowerFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-4px) rotate(10deg); }
}

.deco-line {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #FFB5B5, transparent);
  border-radius: 1px;
}

.header {
  background: linear-gradient(180deg, #FFFFFF 0%, #FFF9F9 100%);
  box-shadow: 0 4px 20px rgba(255, 182, 193, 0.12);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  z-index: 10;
  border-bottom: 2px solid #FFF0F0;
  position: relative;
}

.header::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 182, 193, 0.3) 50%, transparent 100%);
}

.breadcrumb-item {
  font-size: 16px;
  font-weight: 500;
  color: #5D4E4E;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  border: 1px solid rgba(255, 181, 181, 0.3);
}

.user-dropdown:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 107, 107, 0.15);
}

.avatar-animated {
  transition: all 0.3s ease;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.user-dropdown:hover .avatar-animated {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.25);
}

.user-info {
  display: flex;
  flex-direction: column;
  margin-left: 12px;
  margin-right: 8px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #5D4E4E;
}

.user-role {
  font-size: 12px;
  color: #FF6B6B;
  font-weight: 500;
}

.dropdown-arrow {
  color: #FF6B6B;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu-animated {
  border-radius: 16px !important;
  padding: 8px !important;
  box-shadow: 0 12px 40px rgba(255, 107, 107, 0.12) !important;
  border: none !important;
  background: rgba(255, 255, 255, 0.98) !important;
}

.dropdown-menu-animated .el-dropdown-menu__item {
  border-radius: 12px;
  margin: 4px 0;
  transition: all 0.3s ease;
  color: #5D4E4E;
}

.dropdown-menu-animated .el-dropdown-menu__item:hover {
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  color: #FF6B6B;
  transform: translateX(4px);
}

.main-content {
  background: linear-gradient(180deg, #FFFDFB 0%, #FFF8F5 100%);
  padding: 24px;
  position: relative;
  min-height: calc(100vh - 60px);
  overflow: hidden;
}

.content-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.cloud {
  position: absolute;
  animation: floatCloud 20s ease-in-out infinite;
}

.cloud-1 {
  top: 10%;
  left: -100px;
  animation-delay: 0s;
}

.cloud-2 {
  top: 30%;
  right: -80px;
  animation-delay: -10s;
  transform: scale(0.6);
}

.cloud-body {
  width: 100px;
  height: 35px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 35px;
  position: relative;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.2);
}

.cloud-body::before,
.cloud-body::after {
  content: '';
  position: absolute;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
}

.cloud-body::before {
  width: 40px;
  height: 40px;
  top: -20px;
  left: 15px;
}

.cloud-body::after {
  width: 28px;
  height: 28px;
  top: -12px;
  right: 15px;
}

@keyframes floatCloud {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(30px); }
}

.star {
  position: absolute;
  font-size: 14px;
  color: #FFD700;
  animation: twinkle 3s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.5s);
  opacity: 0.5;
}

.star:nth-child(1) { top: 15%; left: 10%; }
.star:nth-child(2) { top: 25%; right: 15%; }
.star:nth-child(3) { top: 45%; left: 5%; }
.star:nth-child(4) { top: 60%; right: 10%; }
.star:nth-child(5) { top: 75%; left: 20%; }
.star:nth-child(6) { top: 35%; right: 30%; }

@keyframes twinkle {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.2); }
}

.page-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.page-leave-active {
  transition: all 0.2s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.back-top-animated {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.25) !important;
  transition: all 0.3s ease !important;
}

.back-top-animated:hover {
  transform: scale(1.15) !important;
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35) !important;
}

.back-top-animated span {
  color: #fff;
  font-size: 18px;
}

@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
