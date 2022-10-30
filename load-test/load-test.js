import http from 'k6/http';
import { check } from "k6";
import { sleep } from 'k6';

export let options = {
  stages: [
      // Ramp-up from 1 to 5 VUs in 5s
      { duration: "5s", target: 1000 },
      // Stay at rest on 5 VUs for 10s
      { duration: "60s", target: 1000 },
      // Ramp-down from 5 to 0 VUs for 5s
      { duration: "5s", target: 0 }
  ]
};
export default function () {
  sleep(1);
  const response = http.get("http://ticketz-web:8080/reserve/A", {headers: {Accepts: "application/json"}});
  check(response, { "status is 200": (r) => r.status === 200 });
  
};
