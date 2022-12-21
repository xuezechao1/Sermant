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
        <p style="margin: 0px">Consumer</p>
      </div>
      <div style="float: right; margin: 50px">
        <img src="../assets/svg/server.svg" class="bar" ref="provider" />
        <p style="margin: 0px">Provider</p>
      </div>
    </div>
    <div style="text-align: center; clear: both; margin: 100px">
      <div style="margin: 50px">
        <img src="../assets/svg/httpdns.svg" ref="gateway" class="bar" />
        <p style="margin: 0px">网关</p>
      </div>
      <div style="float: right; margin: 50px">
        <img src="../assets/svg/server.svg" class="bar" ref="providerB" />
        <p style="margin: 0px">其他服务</p>
      </div>
    </div>
  </div>
  <div style="width: 20%; height: inherit; float: right; overflow: auto" v-scrollBottom>
    <div v-for="item in events" style="margin: 5px">
      <el-alert
        title="item.timeStamp"
        description="This is a description."
        :closable="false"
      />
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
      consumerToZookeeperLine: null,
      providerToZookeeperLine: null,
      consumerToProviderLine: null,
      consumerToGatewayLine: null,
      gatewayToProviderLine: null,
      gatewayToProviderBLine: null,
      timer: null,
      flag: true,
      events: [],
    };
  },
  mounted() {
    this.drawConsumerToGatewayLine();
    this.drawConsumerToProviderLine();
    this.drawConsumerToZookeeperLine();
    this.drawGatewayToProviderLine();
    this.drawGatewayToProviderBLine();
    this.drawProviderToZookeeperLine();
    // 检查注册状态
    axios.get(`${window.location.origin}/getMessage`).then((res) => {
      if (res.data.consumer.status) {
        this.consumerToZookeeperLine.show("draw");
      }
      if (res.data.consumer.status) {
        this.providerToZookeeperLine.show("draw");
      }
    });
  },
  methods: {
    addEvent(event) {
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
    dealWithGateway(destinationWithSermant) {
      this.consumerToGatewayLine.show();
      if (destinationWithSermant) {
        this.gatewayToProviderLine.show();
      } else {
        this.gatewayToProviderBLine.show();
      }
    },
    dealWithSermant() {
      this.consumerToProviderLine.show();
    },
    hideAllLine() {
      this.consumerToGatewayLine.hide();
      this.gatewayToProviderLine.hide();
      this.gatewayToProviderBLine.hide();
      this.consumerToProviderLine.hide();
    },
    showAllLine() {
      this.consumerToGatewayLine.show();
      this.gatewayToProviderLine.show();
      this.gatewayToProviderBLine.show();
      this.consumerToProviderLine.show();
      this.consumerToZookeeperLine.show("draw");
      this.providerToZookeeperLine.show("draw");
    },
    showEvent(title, description) {
      ElNotification({
        title: title,
        message: h("i", { style: "color: teal" }, description),
      });
    },
    newRequest() {
    //   this.showAllLine();
      axios.get(`${window.location.origin}/getMessage`).then((res) => {
        if (res.data == null) {
          return;
        }
        // 处理请求数据
        if (res.data.eventType == "request") {
          if (res.data.eventType.isEnhance) {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.dealWithSermant();
          } else {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.dealWithGateway(res.data.destinationWithSermant);
          }
          return;
        }
        // 处理注册
        if (res.data.eventType == "register") {
          if (res.data.role == "consumer") {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.consumerToZookeeperLine.show("draw");
          } else {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.providerToZookeeperLine.show("draw");
          }
          return;
        }
        // 处理取消注册
        if (res.data.eventType == "unRegister") {
          if (res.data.role == "consumer") {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.consumerToZookeeperLine.hide("draw");
          } else {
            this.showEvent(res.data.timeStamp, res.data.description);
            this.providerToZookeeperLine.hide("draw");
          }
          return;
        }
      });
      this.hideAllLine();
    },
  },
  created() {
    this.timer = setInterval(() => {
      this.newRequest();
    }, 500);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
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
