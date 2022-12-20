<template>
  <div style="width: 100%; height: 100%">
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
export default {
  data() {
    return {
      consumerToZookeeperLine: null,
      providerToZookeeperLine: null,
      consumerToProviderLine: null,
      consumerToGatewayLine: null,
      gatewayToProviderLine: null,
      timer: null,
      flag:true
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
          startPlugColor: "#ff3792",
          endPlugColor: "#fff386",
          gradient: true,
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
          startPlugColor: "#ff3792",
          endPlugColor: "#fff386",
          gradient: true,
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
          startPlugColor: "#ff3792",
          endPlugColor: "#fff386",
          gradient: true,
          hide:true,
          dash: { animation: true },
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
          startPlugColor: "#ff3792",
          endPlugColor: "#fff386",
          gradient: true,
          hide:true,
          dash: { animation: true },
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
          startPlugColor: "#ff3792",
          endPlugColor: "#fff386",
          gradient: true,
          hide:true,
          dash: { animation: true },
        }
      );
    },
    dealWithGateway(){
        this.consumerToGatewayLine.show();
        this.gatewayToProviderLine.show();
    },
    dealWithSermant(){
        this.consumerToProviderLine.show();
    },
    newRequest(){
        this.consumerToGatewayLine.hide();
        this.gatewayToProviderLine.hide();
        this.consumerToProviderLine.hide();
        const res = axios.get(`${window.location.origin}/getMessage`);
        if(res.data == null){   
            return;
        }
        if(res.data.eventType == 'request'){
            if(res.data.eventType.enhance){
                this.dealWithSermant();
            }else{
                this.dealWithGateway();
            }
        }else if(res.data.eventType == 'registry'){
            if(res.data.role == 'consumer'){
                this.consumerToZookeeperLine.show();
            }else{
                this.providerToZookeeperLine.show();
            }
        }
    }
  },
  created() {
    this.timer = setInterval(() => {
        this.newRequest();
    }, 500);
  },
  beforeDestroy() {
    if(this.timer){
        clearInterval(this.timer);
        this.timer = null;
    }
  },
};
</script>

<style lang="less" scoped>
.bar {
  background-color: #0068e6;
  border-radius: 10px;
  margin: 100px;
  height: 50px;
  width: 150px;
}
</style>
