<template>
  <el-config-provider :locale="zhCn" namespace="el">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<style>
html, body, #app {
  height: 100%;
  background-color: #121212;
}
</style>
