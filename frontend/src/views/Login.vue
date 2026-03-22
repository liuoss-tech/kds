<template>
  <div class="login-wrapper">
    <div class="bg-layer">
      <div class="cloud cloud-1">
        <div class="cloud-body"></div>
        <div class="cloud-shadow"></div>
      </div>
      <div class="cloud cloud-2">
        <div class="cloud-body"></div>
      </div>
      <div class="cloud cloud-3">
        <div class="cloud-body"></div>
      </div>
      <div class="stars">
        <span class="star" v-for="i in 12" :key="i" :style="{ '--i': i }">★</span>
      </div>
      <div class="rainbow">
        <div class="rainbow-arc"></div>
      </div>
      <div class="bubbles">
        <span class="bubble" v-for="i in 8" :key="i" :style="{ '--i': i }"></span>
      </div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="card-decoration card-decoration-left">✿</div>
        <div class="card-decoration card-decoration-right">❀</div>
        <div class="card-shine"></div>
        
        <div class="login-header">
          <div class="logo-wrapper">
            <div class="logo-emoji-group">
              <span class="emoji emoji-1">🏠</span>
              <span class="emoji emoji-2">🌸</span>
              <span class="emoji emoji-3">⭐</span>
            </div>
            <div class="logo-glow"></div>
          </div>
          <h1 class="title">幼儿园家校联系系统</h1>
          <p class="subtitle">用心陪伴 · 快乐成长</p>
        </div>

        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="loginRules" 
          @keyup.enter.native="handleLogin"
          class="login-form"
        >
          <el-form-item prop="username" class="form-item">
            <div class="input-wrapper">
              <el-icon class="input-icon"><component :is="'PhoneFilled'" /></el-icon>
              <input 
                v-model="loginForm.username" 
                type="text"
                placeholder="请输入手机号或账号"
                class="custom-input"
                autocomplete="username"
              />
              <div class="input-line"></div>
            </div>
          </el-form-item>

          <el-form-item prop="password" class="form-item">
            <div class="input-wrapper">
              <el-icon class="input-icon"><component :is="'Lock'" /></el-icon>
              <input 
                v-model="loginForm.password" 
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="custom-input"
                autocomplete="current-password"
              />
              <button 
                type="button" 
                class="toggle-password"
                @click="showPassword = !showPassword"
              >
                <el-icon v-if="showPassword"><component :is="'View'" /></el-icon>
                <el-icon v-else><component :is="'Hide'" /></el-icon>
              </button>
              <div class="input-line"></div>
            </div>
          </el-form-item>

          <div class="form-extras">
            <label class="remember-me">
              <input type="checkbox" v-model="loginForm.rememberMe" />
              <span class="checkbox-custom"></span>
              <span class="checkbox-label">记住账号</span>
            </label>
            <a href="javascript:;" class="forgot-link">忘记密码？</a>
          </div>

          <el-form-item class="submit-item">
            <button 
              type="button"
              class="login-btn"
              :class="{ 'is-loading': loading }"
              :disabled="loading"
              @click="handleLogin"
            >
              <span class="btn-content">
                <span class="btn-text" v-if="!loading">
                  <span class="btn-emoji">🎈</span>
                  立即登录
                </span>
                <span class="btn-loader" v-else>
                  <span class="loader-ball ball-1"></span>
                  <span class="loader-ball ball-2"></span>
                  <span class="loader-ball ball-3"></span>
                </span>
              </span>
              <span class="btn-shine"></span>
              <span class="btn-bubbles">
                <span class="btn-bubble" v-for="i in 6" :key="i"></span>
              </span>
            </button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>登录即表示同意 <a href="javascript:;">《用户协议》</a> 和 <a href="javascript:;">《隐私政策》</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import { PhoneFilled, Lock, View, Hide } from '@element-plus/icons-vue';

export default {
  name: 'Login',
  components: {
    PhoneFilled,
    Lock,
    View,
    Hide
  },
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false
      },
      loginRules: {
        username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
      },
      loading: false,
      showPassword: false
    };
  },
  mounted() {
    const savedUsername = localStorage.getItem('kds_saved_username');
    if (savedUsername) {
      this.loginForm.username = savedUsername;
      this.loginForm.rememberMe = true;
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginFormRef.validate((valid) => {
        if (valid) {
          this.loading = true;
          
          request.post('/api/auth/login', {
            username: this.loginForm.username,
            password: this.loginForm.password
          }).then(res => {
            if (this.loginForm.rememberMe) {
              localStorage.setItem('kds_saved_username', this.loginForm.username);
            } else {
              localStorage.removeItem('kds_saved_username');
            }

            this.$notify({
              title: '欢迎回来',
              message: '登录成功，正在跳转...',
              type: 'success',
              position: 'bottom-right',
              duration: 2000
            });
            
            const loginVO = res.data;
            localStorage.setItem('kds_token', loginVO.token);
            localStorage.setItem('kds_role', loginVO.roleCode);
            localStorage.setItem('kds_realName', loginVO.realName);
            localStorage.setItem('kds_userId', loginVO.userId);
            localStorage.setItem('kds_userType', loginVO.userType);

            setTimeout(() => {
              this.$router.push('/dashboard');
            }, 500);
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.login-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-family: 'Noto Sans SC', 'ZCOOL KuaiLe', sans-serif;
}

.bg-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  background: linear-gradient(180deg, #FFF9E6 0%, #FFE4CC 50%, #FFD4E5 100%);
}

.cloud {
  position: absolute;
  animation: floatCloud 25s ease-in-out infinite;
}

.cloud-1 {
  top: 8%;
  left: 5%;
  animation-delay: 0s;
}

.cloud-2 {
  top: 15%;
  right: 10%;
  animation-delay: -8s;
  transform: scale(0.7);
}

.cloud-3 {
  top: 5%;
  right: 25%;
  animation-delay: -15s;
  transform: scale(0.5);
}

.cloud-body {
  width: 120px;
  height: 40px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  position: relative;
  box-shadow: 0 8px 20px rgba(255, 182, 193, 0.3);
}

.cloud-body::before,
.cloud-body::after {
  content: '';
  position: absolute;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
}

.cloud-body::before {
  width: 50px;
  height: 50px;
  top: -25px;
  left: 20px;
}

.cloud-body::after {
  width: 35px;
  height: 35px;
  top: -15px;
  right: 20px;
}

@keyframes floatCloud {
  0%, 100% { transform: translateX(0) translateY(0); }
  25% { transform: translateX(20px) translateY(-10px); }
  50% { transform: translateX(-10px) translateY(5px); }
  75% { transform: translateX(15px) translateY(-5px); }
}

.stars {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.star {
  position: absolute;
  font-size: 14px;
  color: #FFD700;
  animation: twinkle 3s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.3s);
  opacity: 0.6;
}

.star:nth-child(1) { top: 10%; left: 15%; }
.star:nth-child(2) { top: 20%; left: 80%; }
.star:nth-child(3) { top: 35%; left: 10%; }
.star:nth-child(4) { top: 45%; left: 85%; }
.star:nth-child(5) { top: 60%; left: 5%; }
.star:nth-child(6) { top: 70%; left: 90%; }
.star:nth-child(7) { top: 80%; left: 20%; }
.star:nth-child(8) { top: 25%; left: 45%; }
.star:nth-child(9) { top: 55%; left: 75%; }
.star:nth-child(10) { top: 75%; left: 60%; }
.star:nth-child(11) { top: 15%; left: 60%; }
.star:nth-child(12) { top: 65%; left: 35%; }

@keyframes twinkle {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.9; transform: scale(1.2); }
}

.rainbow {
  position: absolute;
  bottom: 5%;
  right: -50px;
  width: 300px;
  height: 150px;
  overflow: hidden;
  opacity: 0.4;
}

.rainbow-arc {
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: conic-gradient(
    from 180deg,
    #FF6B6B, #FF8E53, #FFD93D, #6BCB77, #4D96FF, #9B59B6, #FF6B6B
  );
  animation: rainbowRotate 20s linear infinite;
}

@keyframes rainbowRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.bubbles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bubble {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.8), rgba(255, 182, 193, 0.3));
  animation: bubbleFloat 15s ease-in-out infinite;
  animation-delay: calc(var(--i) * 1s);
}

.bubble:nth-child(1) { width: 20px; height: 20px; bottom: 10%; left: 10%; }
.bubble:nth-child(2) { width: 15px; height: 15px; bottom: 20%; left: 20%; }
.bubble:nth-child(3) { width: 25px; height: 25px; bottom: 15%; left: 70%; }
.bubble:nth-child(4) { width: 12px; height: 12px; bottom: 30%; left: 85%; }
.bubble:nth-child(5) { width: 18px; height: 18px; bottom: 25%; left: 45%; }
.bubble:nth-child(6) { width: 22px; height: 22px; bottom: 5%; left: 55%; }
.bubble:nth-child(7) { width: 10px; height: 10px; bottom: 35%; left: 30%; }
.bubble:nth-child(8) { width: 16px; height: 16px; bottom: 40%; left: 60%; }

@keyframes bubbleFloat {
  0%, 100% { transform: translateY(0) translateX(0); opacity: 0.6; }
  50% { transform: translateY(-30px) translateX(10px); opacity: 0.9; }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 20px;
}

.login-card {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 32px;
  padding: 48px 40px;
  box-shadow: 
    0 20px 60px rgba(255, 182, 193, 0.3),
    0 8px 20px rgba(255, 192, 203, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  animation: cardEntrance 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #FF6B6B, #FF8E53, #FFD93D, #6BCB77, #4D96FF, #9B59B6);
  background-size: 200% 100%;
  animation: gradientFlow 3s linear infinite;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.card-decoration {
  position: absolute;
  font-size: 28px;
  opacity: 0.3;
  animation: decorateFloat 4s ease-in-out infinite;
}

.card-decoration-left {
  top: 20px;
  left: 20px;
  animation-delay: 0s;
}

.card-decoration-right {
  bottom: 20px;
  right: 20px;
  animation-delay: -2s;
}

@keyframes decorateFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-5px) rotate(10deg); }
}

.card-shine {
  position: absolute;
  top: -30%;
  left: -30%;
  width: 160%;
  height: 160%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.4) 0%, transparent 50%);
  pointer-events: none;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.logo-emoji-group {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.emoji {
  font-size: 36px;
  display: inline-block;
  animation: emojiBounce 2s ease-in-out infinite;
}

.emoji-1 {
  animation-delay: 0s;
}

.emoji-2 {
  font-size: 42px;
  animation-delay: 0.3s;
}

.emoji-3 {
  animation-delay: 0.6s;
}

@keyframes emojiBounce {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10px) scale(1.1); }
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 60px;
  background: radial-gradient(ellipse, rgba(255, 217, 61, 0.4) 0%, transparent 70%);
  animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.9; }
}

.title {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #5D4E4E;
  margin: 0 0 8px;
  letter-spacing: 2px;
  text-shadow: 2px 2px 4px rgba(255, 182, 193, 0.3);
}

.subtitle {
  font-size: 14px;
  color: #9B8E8E;
  margin: 0;
  letter-spacing: 4px;
}

.login-form {
  margin-bottom: 24px;
}

.form-item {
  width: 100%;
  margin-bottom: 28px;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #CCB8B8;
  z-index: 1;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.input-wrapper:focus-within .input-icon {
  color: #FF6B6B;
  transform: translateY(-50%) scale(1.1);
}

.custom-input {
  width: 100%;
  height: 56px;
  padding: 0 50px 0 48px;
  background: #FFF9F5;
  border: 2px solid #F0E0E0;
  border-radius: 16px;
  color: #5D4E4E;
  font-size: 15px;
  font-family: inherit;
  outline: none;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.custom-input::placeholder {
  color: #CCB8B8;
}

.custom-input:focus {
  background: #FFFFFF;
  border-color: #FFB5B5;
  box-shadow: 0 0 0 4px rgba(255, 107, 107, 0.15), 0 8px 20px rgba(255, 182, 193, 0.2);
}

.input-line {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #FF6B6B, #FF8E53, #FFD93D);
  border-radius: 2px;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  transform: translateX(-50%);
}

.input-wrapper:focus-within .input-line {
  width: 100%;
}

.toggle-password {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #CCB8B8;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.toggle-password:hover {
  color: #FF6B6B;
}

.form-extras {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 4px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.remember-me input {
  display: none;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
  border: 2px solid #E0C0C0;
  border-radius: 6px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.remember-me input:checked + .checkbox-custom {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-color: transparent;
  transform: scale(1.1);
}

.remember-me input:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 3px;
  width: 5px;
  height: 9px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label {
  font-size: 13px;
  color: #9B8E8E;
}

.forgot-link {
  font-size: 13px;
  color: #FF8E8E;
  text-decoration: none;
  transition: all 0.3s;
}

.forgot-link:hover {
  color: #FF6B6B;
  transform: translateX(3px);
}

.submit-item {
  margin-bottom: 0;
}

.login-btn {
  position: relative;
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 25%, #FFD93D 50%, #6BCB77 75%, #4D96FF 100%);
  background-size: 300% 100%;
  border: none;
  border-radius: 16px;
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  font-family: inherit;
  letter-spacing: 3px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35);
  animation: btnGradient 4s ease infinite;
}

@keyframes btnGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 15px 35px rgba(255, 107, 107, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.login-btn:disabled {
  cursor: not-allowed;
  opacity: 0.9;
}

.btn-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-text {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-emoji {
  font-size: 20px;
  animation: emojiWave 1s ease-in-out infinite;
}

@keyframes emojiWave {
  0%, 100% { transform: rotate(-10deg); }
  50% { transform: rotate(10deg); }
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s;
}

.login-btn:hover .btn-shine {
  left: 100%;
}

.btn-bubbles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.btn-bubble {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  opacity: 0;
}

.login-btn:hover .btn-bubble {
  animation: bubblePop 1s ease-out infinite;
}

.btn-bubble:nth-child(1) { left: 10%; bottom: 0; animation-delay: 0s; }
.btn-bubble:nth-child(2) { left: 25%; bottom: 0; animation-delay: 0.1s; }
.btn-bubble:nth-child(3) { left: 40%; bottom: 0; animation-delay: 0.2s; }
.btn-bubble:nth-child(4) { left: 55%; bottom: 0; animation-delay: 0.3s; }
.btn-bubble:nth-child(5) { left: 70%; bottom: 0; animation-delay: 0.4s; }
.btn-bubble:nth-child(6) { left: 85%; bottom: 0; animation-delay: 0.5s; }

@keyframes bubblePop {
  0% { transform: translateY(0) scale(1); opacity: 0.6; }
  100% { transform: translateY(-50px) scale(0); opacity: 0; }
}

.btn-loader {
  display: flex;
  gap: 8px;
  align-items: center;
}

.loader-ball {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: ballBounce 1s ease-in-out infinite;
}

.loader-ball.ball-1 {
  background: #FF6B6B;
  animation-delay: 0s;
}

.loader-ball.ball-2 {
  background: #FFD93D;
  animation-delay: 0.15s;
}

.loader-ball.ball-3 {
  background: #6BCB77;
  animation-delay: 0.3s;
}

@keyframes ballBounce {
  0%, 80%, 100% { 
    transform: translateY(0) scale(0.8); 
    opacity: 0.5;
  }
  40% { 
    transform: translateY(-10px) scale(1); 
    opacity: 1;
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed #F0E0E0;
}

.login-footer p {
  font-size: 12px;
  color: #CCB8B8;
  margin: 0;
}

.login-footer a {
  color: #FF8E8E;
  text-decoration: none;
  transition: all 0.3s;
}

.login-footer a:hover {
  color: #FF6B6B;
}

@media screen and (max-width: 480px) {
  .login-card {
    padding: 36px 24px;
    border-radius: 24px;
  }
  
  .title {
    font-size: 22px;
  }
  
  .emoji {
    font-size: 28px;
  }
  
  .emoji-2 {
    font-size: 34px;
  }
  
  .custom-input {
    height: 52px;
  }
  
  .login-btn {
    height: 52px;
  }
}

@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation: none !important;
    transition: none !important;
  }
}
</style>
