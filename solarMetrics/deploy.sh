sudo mv /home/yannick/mitterweg7/solarMetrics/solarMetrics.service /etc/systemd/system/solarMetrics.service
sudo systemctl daemon-reload
sudo systemctl restart solarMetrics.service
