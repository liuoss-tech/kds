<template>
  <div class="message-container">
    <div class="conversation-list">
      <div class="list-header">
        <div class="header-content">
          <span class="header-icon">💬</span>
          <span class="title">私信消息</span>
        </div>
        <el-button type="primary" size="small" class="cute-btn" @click="openContactSelector">
          <span>➕</span> 新建
        </el-button>
      </div>
      <div class="search-box">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索联系人..." 
          class="cute-input"
          clearable 
        >
          <template #prefix>
            <span class="search-icon">🔍</span>
          </template>
        </el-input>
      </div>
      <div class="conversation-items" v-if="conversations.length > 0">
        <div
          v-for="conv in filteredConversations"
          :key="conv.contactUserId"
          class="conversation-item"
          :class="{ active: currentContactId === conv.contactUserId }"
          @click="selectConversation(conv)"
        >
          <div class="avatar-wrapper">
            <div class="avatar">
              {{ conv.contactUserName?.charAt(0) || '用户' }}
            </div>
            <span class="online-dot" v-if="conv.online"></span>
          </div>
          <div class="conv-info">
            <div class="conv-header">
              <span class="conv-name">{{ conv.contactUserName }}</span>
              <span class="conv-time">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <div class="conv-preview-row">
              <span class="conv-preview">{{ conv.lastMessage }}</span>
              <span class="unread-badge" v-if="conv.unreadCount > 0">{{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-state" v-else>
        <div class="empty-icon">🌸</div>
        <p class="empty-title">暂无私信消息</p>
        <p class="empty-tip">点击右上角"新建"开始聊天吧</p>
      </div>
    </div>

    <div class="chat-window">
      <template v-if="currentContact">
        <div class="chat-header">
          <div class="contact-info">
            <div class="contact-avatar">
              {{ currentContact.contactUserName?.charAt(0) || '用户' }}
            </div>
            <div class="contact-details">
              <span class="contact-name">{{ currentContact.contactUserName }}</span>
              <span class="contact-type">
                <span class="type-icon">{{ currentContact.contactUserType === 1 ? '👩‍🏫' : '👨‍👩' }}</span>
                {{ currentContact.contactUserType === 1 ? '老师' : '家长' }}
              </span>
            </div>
          </div>
          <el-button size="small" class="close-btn" @click="closeChat">关闭</el-button>
        </div>
        <div class="message-list" ref="messageListRef">
          <div class="messages-wrapper">
            <div
              v-for="msg in currentMessages"
              :key="msg.id"
              class="message-item"
              :class="{ mine: msg.senderId === currentUserId }"
            >
              <div class="message-bubble" :class="{ mine: msg.senderId === currentUserId }">
                <div class="message-content">{{ msg.content }}</div>
                <div class="message-meta">
                  <span class="message-time">{{ formatMessageTime(msg.createTime) }}</span>
                  <span class="message-status" :class="{ read: msg.isRead }">
                    {{ msg.isRead ? '✓✓' : '✓' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="message-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              placeholder="输入消息... (Ctrl+Enter 发送)"
              @keyup.enter.ctrl="sendMessage"
              class="cute-textarea"
              resize="none"
            />
          </div>
          <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">
            <span>🚀</span> 发送
          </el-button>
        </div>
      </template>
      <div class="chat-empty" v-else>
        <div class="empty-illustration">
          <span class="chat-emoji">💌</span>
          <div class="floating-element elem-1">✨</div>
          <div class="floating-element elem-2">🌸</div>
          <div class="floating-element elem-3">⭐</div>
        </div>
        <p class="empty-title">选择或搜索联系人开始聊天</p>
        <p class="empty-tip">老师们和家长们可以便捷沟通啦</p>
      </div>
    </div>

    <el-dialog 
      v-model="showContactSelector" 
      title="选择联系人" 
      width="600px" 
      class="cute-dialog"
    >
      <div class="contact-selector">
        <el-tabs v-model="selectorTab" type="border-card" class="cute-tabs">
          <el-tab-pane label="按班级选择" name="class" v-if="isTeacher">
            <div v-for="item in contacts" :key="item.classId" class="class-section">
              <div class="class-header">
                <span class="class-icon">🏫</span>
                <span class="class-name">{{ item.className }}</span>
              </div>
              <div class="parent-list">
                <div
                  v-for="parent in item.parents"
                  :key="parent.userId"
                  class="contact-item"
                  @click="startChat(parent.userId, parent.realName)"
                >
                  <div class="contact-avatar small">
                    {{ parent.realName?.charAt(0) || '家' }}
                  </div>
                  <span class="contact-name">{{ parent.realName }}</span>
                  <span class="relation-tag">{{ parent.relation }}</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="按孩子选择" name="child" v-if="!isTeacher">
            <div v-for="item in contacts" :key="item.studentId" class="class-section">
              <div class="class-header">
                <span class="class-icon">👶</span>
                <span class="class-name">{{ item.studentName }} - {{ item.className }}</span>
              </div>
              <div class="parent-list">
                <div
                  v-for="teacher in item.teachers"
                  :key="teacher.userId"
                  class="contact-item"
                  @click="startChat(teacher.userId, teacher.realName)"
                >
                  <div class="contact-avatar small">
                    {{ teacher.realName?.charAt(0) || '老' }}
                  </div>
                  <span class="contact-name">{{ teacher.realName }}</span>
                  <span class="relation-tag teacher">
                    {{ teacher.isHeadTeacher ? '🌟 班主任' : '👩‍🏫 老师' }}
                  </span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const currentUserId = ref(parseInt(localStorage.getItem('kds_userId') || '0'))
const userType = ref(parseInt(localStorage.getItem('kds_userType') || '0'))
const isTeacher = computed(() => userType.value === 1)

const conversations = ref([])
const currentContactId = ref(null)
const currentContact = ref(null)
const currentMessages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)
const showContactSelector = ref(false)
const selectorTab = ref('class')
const contacts = ref([])
const searchKeyword = ref('')
let pollTimer = null

const filteredConversations = computed(() => {
  if (!searchKeyword.value) return conversations.value
  return conversations.value.filter(c =>
    c.contactUserName.includes(searchKeyword.value)
  )
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const formatMessageTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const loadConversations = async () => {
  try {
    const res = await request.get('/api/busi/message/conversations')
    if (res.code === 200) {
      conversations.value = res.data || []
    }
  } catch (e) {
    console.error('加载会话列表失败', e)
  }
}

const loadContacts = async () => {
  try {
    const res = await request.get('/api/busi/message/contacts')
    if (res.code === 200) {
      contacts.value = res.data || []
    }
  } catch (e) {
    console.error('加载联系人失败', e)
  }
}

const openContactSelector = async () => {
  await loadContacts()
  showContactSelector.value = true
}

const loadMessages = async (contactId) => {
  try {
    const res = await request.get('/api/busi/message/history', {
      params: { contactUserId: contactId, page: 1, size: 100 }
    })
    if (res.code === 200) {
      currentMessages.value = res.data.records || []
      await nextTick()
      scrollToBottom()
    }
  } catch (e) {
    console.error('加载消息失败', e)
  }
}

const selectConversation = async (conv) => {
  currentContactId.value = conv.contactUserId
  currentContact.value = conv
  await loadMessages(conv.contactUserId)
  await markAsRead(conv.contactUserId)
  await loadConversations()
}

const startChat = async (userId, userName) => {
  showContactSelector.value = false
  const existing = conversations.value.find(c => c.contactUserId === userId)
  if (existing) {
    await selectConversation(existing)
  } else {
    currentContactId.value = userId
    currentContact.value = {
      contactUserId: userId,
      contactUserName: userName,
      contactUserType: isTeacher.value ? 2 : 1
    }
    currentMessages.value = []
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !currentContactId.value) return
  try {
    const res = await request.post('/api/busi/message/send', {
      receiverId: currentContactId.value,
      content: inputMessage.value.trim()
    })
    if (res.code === 200) {
      inputMessage.value = ''
      await loadMessages(currentContactId.value)
      await loadConversations()
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

const markAsRead = async (contactUserId) => {
  try {
    await request.post('/api/busi/message/mark-read', { contactUserId })
  } catch (e) {
    console.error('标记已读失败', e)
  }
}

const closeChat = () => {
  currentContactId.value = null
  currentContact.value = null
  currentMessages.value = []
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const startPolling = () => {
  pollTimer = setInterval(async () => {
    await loadConversations()
    if (currentContactId.value) {
      await loadMessages(currentContactId.value)
    }
  }, 10000)
}

onMounted(async () => {
  await loadConversations()
  await loadContacts()
  startPolling()
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.message-container {
  display: flex;
  height: calc(100vh - 120px);
  background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(255, 182, 193, 0.2);
  font-family: 'Noto Sans SC', sans-serif;
}

.conversation-list {
  width: 320px;
  background: linear-gradient(180deg, #FFFFFF 0%, #FFF9F9 100%);
  border-right: 1px solid rgba(255, 181, 181, 0.2);
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #FFB5B5 0%, #FFD4D4 100%);
  border-bottom: none;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 20px;
}

.list-header .title {
  font-family: 'ZCOOL KuaiLe', sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: #5D4E4E;
}

.cute-btn {
  border-radius: 20px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border: none;
  color: #fff;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.cute-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.35);
}

.search-box {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 181, 181, 0.15);
}

.cute-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  box-shadow: 0 0 0 1px rgba(255, 181, 181, 0.3) inset;
  background: #FFF9F9;
  transition: all 0.3s;
}

.cute-input :deep(.el-input__wrapper:hover),
.cute-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2), 0 0 0 1px #FFB5B5 inset;
}

.search-icon {
  font-size: 14px;
}

.conversation-items {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid rgba(255, 181, 181, 0.1);
}

.conversation-item:hover {
  background: rgba(255, 230, 230, 0.4);
}

.conversation-item.active {
  background: linear-gradient(135deg, rgba(255, 181, 181, 0.3) 0%, rgba(255, 217, 217, 0.3) 100%);
  border-left: 3px solid #FF6B6B;
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.25);
}

.online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #6BCB77;
  border: 2px solid #fff;
  border-radius: 50%;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conv-name {
  font-weight: 600;
  color: #5D4E4E;
  font-size: 14px;
}

.conv-time {
  font-size: 11px;
  color: #CCB8B8;
}

.conv-preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conv-preview {
  font-size: 12px;
  color: #9B8E8E;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 140px;
}

.unread-badge {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  color: #fff;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-title {
  font-family: 'ZCOOL KuaiLe', sans-serif;
  font-size: 16px;
  color: #5D4E4E;
  margin: 0 0 8px;
}

.empty-tip {
  font-size: 13px;
  color: #9B8E8E;
  margin: 0;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #FFFDFB 0%, #FFF8F5 100%);
}

.chat-header {
  padding: 16px 20px;
  background: linear-gradient(180deg, #FFFFFF 0%, #FFF9F9 100%);
  border-bottom: 1px solid rgba(255, 181, 181, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.contact-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-name {
  font-weight: 600;
  font-size: 16px;
  color: #5D4E4E;
}

.contact-type {
  font-size: 12px;
  color: #9B8E8E;
  display: flex;
  align-items: center;
  gap: 4px;
}

.type-icon {
  font-size: 12px;
}

.close-btn {
  border-radius: 16px;
  padding: 8px 16px;
  border: 1px solid rgba(255, 181, 181, 0.3);
  color: #9B8E8E;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 230, 230, 0.5);
  color: #FF6B6B;
  border-color: #FF6B6B;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.messages-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
}

.message-item.mine {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-bubble:not(.mine) {
  background: #FFFFFF;
  border: 1px solid rgba(255, 181, 181, 0.2);
  border-bottom-left-radius: 4px;
}

.message-bubble.mine {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  border-bottom-right-radius: 4px;
}

.message-content {
  line-height: 1.5;
  word-break: break-word;
  color: #5D4E4E;
}

.message-bubble.mine .message-content {
  color: #fff;
}

.message-meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.message-time {
  font-size: 10px;
  color: #CCB8B8;
}

.message-bubble.mine .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.message-status {
  font-size: 10px;
  color: #CCB8B8;
}

.message-status.read {
  color: #6BCB77;
}

.message-bubble.mine .message-status.read {
  color: rgba(255, 255, 255, 0.8);
}

.message-input {
  padding: 16px 20px;
  background: #FFFFFF;
  border-top: 1px solid rgba(255, 181, 181, 0.2);
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-wrapper {
  flex: 1;
}

.cute-textarea :deep(.el-textarea__inner) {
  border-radius: 16px;
  border: 1px solid rgba(255, 181, 181, 0.3);
  background: #FFF9F9;
  transition: all 0.3s;
}

.cute-textarea :deep(.el-textarea__inner:hover),
.cute-textarea :deep(.el-textarea__inner:focus) {
  border-color: #FFB5B5;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.send-btn {
  border-radius: 20px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  border: none;
  color: #fff;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex;
  align-items: center;
  gap: 6px;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.35);
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.send-btn:disabled {
  opacity: 0.5;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-illustration {
  position: relative;
  margin-bottom: 24px;
}

.chat-emoji {
  font-size: 72px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.floating-element {
  position: absolute;
  font-size: 20px;
  animation: twinkle 2s ease-in-out infinite;
}

.elem-1 {
  top: -10px;
  right: -20px;
  animation-delay: 0s;
}

.elem-2 {
  bottom: 0;
  left: -30px;
  animation-delay: 0.5s;
}

.elem-3 {
  top: 20px;
  right: -40px;
  animation-delay: 1s;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.chat-empty .empty-title {
  font-family: 'ZCOOL KuaiLe', sans-serif;
  font-size: 18px;
  color: #5D4E4E;
  margin: 0 0 8px;
}

.chat-empty .empty-tip {
  font-size: 14px;
  color: #9B8E8E;
}

.contact-selector {
  min-height: 300px;
}

.cute-tabs :deep(.el-tabs__header) {
  background: #FFF9F9;
  border-radius: 12px 12px 0 0;
}

.cute-tabs :deep(.el-tabs__item) {
  border-radius: 8px 8px 0 0;
  color: #9B8E8E;
}

.cute-tabs :deep(.el-tabs__item.is-active) {
  background: #fff;
  color: #FF6B6B;
}

.class-section {
  margin-bottom: 20px;
}

.class-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  border-radius: 12px;
  margin-bottom: 12px;
}

.class-icon {
  font-size: 16px;
}

.class-name {
  font-weight: 600;
  color: #5D4E4E;
}

.parent-list {
  padding-left: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
}

.contact-item:hover {
  background: rgba(255, 230, 230, 0.5);
  transform: translateX(4px);
}

.contact-avatar.small {
  width: 36px;
  height: 36px;
  font-size: 14px;
}

.contact-item .contact-name {
  flex: 1;
  font-size: 14px;
}

.relation-tag {
  font-size: 12px;
  color: #9B59B6;
  background: rgba(155, 89, 182, 0.1);
  padding: 4px 10px;
  border-radius: 12px;
}

.relation-tag.teacher {
  color: #FF6B6B;
  background: rgba(255, 107, 107, 0.1);
}

.cute-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  padding: 20px;
}

.cute-dialog :deep(.el-dialog__title) {
  font-family: 'ZCOOL KuaiLe', sans-serif;
  color: #5D4E4E;
}

.cute-dialog :deep(.el-dialog__body) {
  padding: 20px;
}
</style>
