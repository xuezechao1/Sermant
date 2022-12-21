<template>
  <div style="width: 80%; height: 100%">
    <div style="text-align: center">
      <button ref="zookeeper" class="bar" :height="100">zookeeper</button>
    </div>
    <div>
      <button style="float: left" ref="consumer" class="bar" :height="100">
        Consumer
      </button>
      <button style="float: right" ref="provider" class="bar" :height="100">
        Provider
      </button>
    </div>
    <div style="text-align: center; clear: both">
      <button ref="gateway" class="bar" :height="100">Gateway</button>
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
      timer: null,
      flag: true,
    };
  },
  mounted() {
    this.drawConsumerToGatewayLine();
    this.drawConsumerToProviderLine();
    this.drawConsumerToZookeeperLine();
    this.drawGatewayToProviderLine();
    this.drawProviderToZookeeperLine();
  },
  methods: {
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
          size: 10
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
          size: 10
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
          size: 10
        }
      );
    },
    dealWithGateway() {
      this.consumerToGatewayLine.show();
      this.gatewayToProviderLine.show();
    },
    dealWithSermant() {
      this.consumerToProviderLine.show();
    },
    hideAllLine() {
      this.consumerToGatewayLine.hide();
      this.gatewayToProviderLine.hide();
      this.consumerToProviderLine.hide();
    },
    showAllLine() {
      this.consumerToGatewayLine.show();
      this.gatewayToProviderLine.show();
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
    //   this.showEvent("aaaa", "bbbbbbbbbb");
    //   this.showAllLine();
      axios.get(`${window.location.origin}/getMessage`).then((res) => {
        if (res.data == null) {
          return;
        }
        if (res.data.eventType == "request") {
          if (res.data.eventType.isEnhance) {
            this.showEvent(res.data.timeStamp,res.data.description);
            this.dealWithSermant();
          } else {
            this.showEvent(res.data.timeStamp,res.data.description);
            this.dealWithGateway();
          }
        } else if (res.data.eventType == "registry") {
          if (res.data.role == "consumer") {
            this.showEvent(res.data.timeStamp,res.data.description);
            this.consumerToZookeeperLine.show("draw");
          } else {
            this.showEvent(res.data.timeStamp,res.data.description);
            this.providerToZookeeperLine.show("draw");
          }
        }
      });
    //   this.hideAllLine();
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
  background-color: teal;
  border: none;
  font-size: 20px;
  color: #ffffff;
  border-radius: 10px;
  margin: 100px;
  height: 50px;
  width: 150px;
}
</style>
