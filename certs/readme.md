此目录是调试、demo目录，展示docker 客户端证书样式。与实际项目运行无关。

## 使用

注意：配置的时候，客户端证书名字必须是：ca.pem，cert.pem， key.pem 。 这是com.github.docker-java里写死的逻辑

### 客户端
- 临时使用
```bash
docker --tlsverify \
  --tlscacert=ca.pem \
  --tlscert=cert.pem \
  --tlskey=key.pem \
  -H tcp://host:2376 \
  info 
  
```
or
```bash
curl --tlsv1.2 --cacert ca.pem --cert cert.pem  --key key.pem https://host:2376/version
```

- 环境变量
```bash
export DOCKER_TLS_VERIFY=1
export DOCKER_CERT_PATH=~/.docker
export DOCKER_HOST=tcp://你的服务器IP:2376

docker info
docker ps
```

### 服务器

/etc/docker/daemon.json 添加配置
```json
{
  "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2376"],
  "tls": true,
  "tlsverify": true,
  "tlscacert": "/etc/docker/certs/ca.pem",
  "tlscert": "/etc/docker/certs/server-cert.pem",
  "tlskey": "/etc/docker/certs/server-key.pem"
}
```

启动docker
```bash
systemctl daemon-reload
systemctl restart docker
```
