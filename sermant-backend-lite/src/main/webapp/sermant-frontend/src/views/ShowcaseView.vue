<template>
  <div style="width: 80%; height: 100%; float: left">
    <div style="text-align: center; margin: 100px">
      <div>
        <img src="../assets/svg/Link.svg" ref="zookeeper" class="bar" />
        <p style="margin: 0px">注册中心</p>
      </div>
    </div>
    <div style="margin: 100px">
      <div style="float: left; margin: 50px">
        <img src="../assets/svg/Web.svg" class="bar" ref="consumer" />
        <p style="margin: 0px;word-break: break-all;width:120px">{{ consumer }}</p>
      </div>
      <div style="float: right; margin: 50px">
        <img src="../assets/svg/server.svg" class="bar" ref="provider" />
        <p style="margin: 0px;word-break: break-all;width:120px">{{ provider }}</p>
      </div>
    </div>
    <div style="text-align: center; clear: both; margin: 100px">
      <div style="margin: 50px">
        <img src="../assets/svg/httpdns.svg" ref="gateway" class="bar" />
        <p style="margin: 0px">总线</p>
      </div>
      <div style="float: right; margin: 50px">
        <img src="../assets/svg/server.svg" class="bar" ref="providerB" />
        <p style="margin: 0px;width:120px">其他服务</p>
      </div>
    </div>
  </div>
  <div style="width: 20%; height: inherit; float: right; overflow: auto" v-scrollBottom>
    <div v-for="item in events" style="margin: 5px">
      <el-card class="box-card">
        <div class="card-header">
          <span style="font-weight: 900">{{ item.timeStamp }}</span>
        </div>
        <p v-if=item.isEnhance style="font-weight: 900;color: #FF0000; margin: 5 px; width: 100%; word-break: break-all">
          当前流量走注册发现架构
        </p>
        <p v-if=!item.isEnhance style="font-weight: 900;color: #FF0000; margin: 5 px; width: 100%; word-break: break-all">
          当前流量走MO总线架构
        </p>
        <p style="color: teal; margin: 5 px; width: 100%; word-break: break-all">
          URL: {{ item.destination }}
        </p>
      </el-card>
    </div>
  </div>
</template>

<script>
import interact from "interactjs";
import LeaderLine from "leader-line-vue";
import axios from "axios";
import { h } from "vue";
import { ElNotification } from "element-plus";
export default {
  data() {
    return {
      consumer: "MOSMService",
      provider: "MODataStrategyEngineService",
      consumerToZookeeperLine: null,
      providerToZookeeperLine: null,
      consumerToProviderLine: null,
      consumerToGatewayLine: null,
      gatewayToProviderLine: null,
      gatewayToProviderBLine: null,
      // 连接示意线
      consumerToProviderLink: null,
      consumerToGatewayLink: null,
      gatewayToProviderLink: null,
      gatewayToProviderBLink: null,
      // 流量线清理标记
      consumerToProviderLineFlag: 2,
      consumerToGatewayLineFlag: 2,
      gatewayToProviderLineFlag: 2,
      gatewayToProviderBLineFlag: 2,
      // 清理流量线计时器
      clearTimer: null,
      timer: null,
      flag: true,
      isZookeeper: true,
      events: [],
    };
  },
  mounted() {
    // 绘制连接示意线
    this.drawConsumerToProviderLink();
    this.drawConsumerToGatewayLink();
    this.drawGatewayToProviderLink();
    this.drawGatewayToProviderBLink();
    // 绘制流量示意线
    this.drawConsumerToGatewayLine();
    this.drawConsumerToProviderLine();
    this.drawConsumerToZookeeperLine();
    this.drawGatewayToProviderLine();
    this.drawGatewayToProviderBLine();
    this.drawProviderToZookeeperLine();
  },
  methods: {
    addEvent(event) {
      if (this.events.length >= 5) {
        this.events.shift();
      }
      this.events.push(event);
    },
    drawConsumerToZookeeperLine() {
      this.consumerToZookeeperLine = LeaderLine.setLine(
        this.$refs.consumer,
        this.$refs.zookeeper,
        {
          startSocket: "top",
          endSocket: "left",
          path: "fluid",
          color: "#999999",
          gradient: true,
          hide: true,
          dash: true,
        }
      );
    },
    drawProviderToZookeeperLine() {
      this.providerToZookeeperLine = LeaderLine.setLine(
        this.$refs.provider,
        this.$refs.zookeeper,
        {
          startSocket: "top",
          endSocket: "right",
          path: "fluid",
          color: "#999999",
          gradient: true,
          hide: true,
          dash: true,
        }
      );
    },
    drawConsumerToProviderLine() {
      this.consumerToProviderLine = LeaderLine.setLine(
        this.$refs.consumer,
        this.$refs.provider,
        {
          startSocket: "right",
          endSocket: "left",
          startSocketGravity: [270, -300],
          endSocketGravity: [-300, -270],
          path: "fluid",
          color: "#339900",
          gradient: true,
          hide: true,
          dash: { animation: true },
          size: 10,
        }
      );
    },
    drawConsumerToGatewayLine() {
      this.consumerToGatewayLine = LeaderLine.setLine(
        this.$refs.consumer,
        this.$refs.gateway,
        {
          startSocket: "bottom",
          endSocket: "left",
          path: "fluid",
          color: "#33CCCC",
          gradient: true,
          hide: true,
          dash: { animation: true },
          size: 10,
        }
      );
    },
    drawGatewayToProviderLine() {
      this.gatewayToProviderLine = LeaderLine.setLine(
        this.$refs.gateway,
        this.$refs.provider,
        {
          startSocket: "right",
          endSocket: "bottom",
          path: "fluid",
          color: "#33CCCC",
          gradient: true,
          hide: true,
          dash: { animation: true },
          size: 10,
        }
      );
    },
    drawGatewayToProviderBLine() {
      this.gatewayToProviderBLine = LeaderLine.setLine(
        this.$refs.gateway,
        this.$refs.providerB,
        {
          startSocket: "right",
          endSocket: "left",
          path: "fluid",
          color: "#33CCCC",
          gradient: true,
          hide: true,
          dash: { animation: true },
          size: 10,
        }
      );
    },
    // 示意线绘图
    drawConsumerToProviderLink() {
      this.consumerToProviderLink = LeaderLine.setLine(
        this.$refs.consumer,
        this.$refs.provider,
        {
          startSocket: "right",
          endSocket: "left",
          startSocketGravity: [270, -300],
          endSocketGravity: [-300, -270],
          path: "fluid",
          color: "#999999",
          gradient: true,
          hide: true,
          dash: true,
        }
      );
    },
    drawConsumerToGatewayLink() {
      this.consumerToGatewayLink = LeaderLine.setLine(
        this.$refs.consumer,
        this.$refs.gateway,
        {
          startSocket: "bottom",
          endSocket: "left",
          path: "fluid",
          color: "#999999",
          gradient: true,
          dash: true,
        }
      );
    },
    drawGatewayToProviderLink() {
      this.gatewayToProviderLink = LeaderLine.setLine(
        this.$refs.gateway,
        this.$refs.provider,
        {
          startSocket: "right",
          endSocket: "bottom",
          path: "fluid",
          color: "#999999",
          gradient: true,
          hide: true,
          dash: true,
        }
      );
    },
    drawGatewayToProviderBLink() {
      this.gatewayToProviderBLink = LeaderLine.setLine(
        this.$refs.gateway,
        this.$refs.providerB,
        {
          startSocket: "right",
          endSocket: "left",
          path: "fluid",
          color: "#999999",
          gradient: true,
          dash: true,
        }
      );
    },
    // 线条处理逻辑
    dealWithGateway(destinationWithSermant) {
      this.consumerToGatewayLine.show();
      this.consumerToGatewayLineFlag = 2;
      if (destinationWithSermant) {
        this.gatewayToProviderLine.show();
        this.gatewayToProviderLineFlag = 2;
      } else {
        this.gatewayToProviderBLine.show();
        this.gatewayToProviderBLineFlag = 2;
      }
    },
    switchGatewayAndZk(isZookeeper) {
      if (isZookeeper) {
        this.gatewayToProviderLink.hide("draw");
        this.consumerToProviderLink.show("draw");
      } else {
        this.consumerToProviderLink.hide("draw");
        this.gatewayToProviderLink.show("draw");
      }
    },
    dealWithSermant() {
      this.consumerToProviderLine.show();
      this.consumerToProviderLineFlag = 2;
    },
    hideAllLine() {
      this.consumerToGatewayLine.hide();
      this.gatewayToProviderLine.hide();
      this.gatewayToProviderBLine.hide();
      this.consumerToProviderLine.hide();
    },
    showAllLine() {
      this.consumerToGatewayLine.show();
      this.consumerToGatewayLineFlag = 2;
      this.gatewayToProviderLine.show();
      this.gatewayToProviderLineFlag = 2;
      this.gatewayToProviderBLine.show();
      this.gatewayToProviderBLineFlag = 2;
      this.consumerToProviderLine.show();
      this.consumerToProviderLineFlag = 2;
      this.consumerToZookeeperLine.show("draw");
      this.providerToZookeeperLine.show("draw");
      this.consumerToGatewayLink.show("draw");
      this.gatewayToProviderBLink.show("draw");
    },
    newRequest() {
      // this.showAllLine();
      // this.switchGatewayAndZk(this.isZookeeper);
      // this.isZookeeper = !this.isZookeeper;
      // 检查注册状态
      axios.get(`${window.location.origin}/getInstanceStatus`).then((res) => {
        if (res.data.consumer.status) {
          this.consumerToZookeeperLine.show("draw");
          // this.consumer = res.data.consumer.serviceName;
        }
        if (res.data.provider.status) {
          this.providerToZookeeperLine.show("draw");
          // this.provider = res.data.consumer.provider;
        }
        this.switchGatewayAndZk(res.data.zkOrGw);
      });
      // 获取事件
      axios.get(`${window.location.origin}/getMessage`).then((res) => {
        if (res.data == null) {
          return;
        }
        // 处理请求数据
        if (res.data.eventType == "request") {
          this.addEvent(res.data);
          if (res.data.isEnhance) {
            this.dealWithSermant();
          } else {
            this.dealWithGateway(res.data.destinationWithSermant);
          }
          return;
        }
        // 处理注册
        if (res.data.eventType == "register") {
          if (res.data.role == "consumer") {
            this.consumerToZookeeperLine.show("draw");
          } else {
            this.providerToZookeeperLine.show("draw");
          }
          return;
        }
        // 处理取消注册
        if (res.data.eventType == "unRegister") {
          if (res.data.role == "consumer") {
            this.consumerToZookeeperLine.hide("draw");
          } else {
            this.providerToZookeeperLine.hide("draw");
          }
          return;
        }
      });
    },
  },
  created() {
    this.timer = setInterval(() => {
      this.newRequest();
    }, 100);
    this.clearTimer = setInterval(() => {
      if (this.consumerToProviderLineFlag > 0) {
        this.consumerToProviderLineFlag = this.consumerToProviderLineFlag - 1;
      } else {
        this.consumerToProviderLine.hide();
      }
      if (this.consumerToGatewayLineFlag > 0) {
        this.consumerToGatewayLineFlag = this.consumerToGatewayLineFlag - 1;
      } else {
        this.consumerToGatewayLine.hide();
      }
      if (this.gatewayToProviderLineFlag > 0) {
        this.gatewayToProviderLineFlag = this.gatewayToProviderLineFlag - 1;
      } else {
        this.gatewayToProviderLine.hide();
      }
      if (this.gatewayToProviderBLineFlag > 0) {
        this.gatewayToProviderBLineFlag = this.gatewayToProviderBLineFlag - 1;
      } else {
        this.gatewayToProviderBLine.hide();
      }
    }, 500);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
      clearInterval(this.clearTimer);
      clearTimer = null;
    }
  },
};
</script>

<style lang="less" scoped>
.bar {
  height: 75px;
  width: 75px;
}
.container {
  height: 100px;
  width: 100px;
}
</style>
