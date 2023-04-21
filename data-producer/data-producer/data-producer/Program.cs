﻿
using Confluent.Kafka;
using Azure.Core;
using System;
using Azure.Identity;


namespace KafkaProducer
{

    public class Program
    {
        private static string eventHubNamespace = "techcamp";


        private static string eventHubNamespaceFQDNwithPort = $"{eventHubNamespace}.servicebus.windows.net:9093";

        //in kafka world this is the topic in event hub is the event hub name under the namespace
        private static string topicName = "test";

        static void OauthCallback(IClient client, string cfg)
        {
            try
            {
                Console.WriteLine("Getting auth token");

                DefaultAzureCredentialOptions secretOptions = new DefaultAzureCredentialOptions();
                DefaultAzureCredential secretCredential = new DefaultAzureCredential(secretOptions);

                var tokenRequestContext = new TokenRequestContext(new string[] { $"https://{eventHubNamespace}.servicebus.windows.net/.default" });

                var accessToken = secretCredential.GetToken(tokenRequestContext);

                client.OAuthBearerSetToken(accessToken.Token, accessToken.ExpiresOn.ToUnixTimeMilliseconds(), null);
            }
            catch (Exception e)
            {
                Console.WriteLine($"ERROR {e.Message}");

                client.OAuthBearerSetTokenFailure(e.ToString());
            }
        }

        static async Task Main(string[] args)
        {
            Console.WriteLine("Hello Event Hub Kafka Client");

            var config = new ProducerConfig
            {
                BootstrapServers = eventHubNamespaceFQDNwithPort,
                SaslMechanism = SaslMechanism.OAuthBearer,
                SecurityProtocol = SecurityProtocol.SaslSsl,
                BrokerVersionFallback = "0.10.0.0",
                ApiVersionRequestTimeoutMs = 10,
                Debug = "security,broker,protocol"
            };

            var configKey = new ProducerConfig
            {
                BootstrapServers = eventHubNamespaceFQDNwithPort,
                SecurityProtocol = SecurityProtocol.SaslSsl,
                SaslMechanism = SaslMechanism.Plain,
                SaslUsername = "$ConnectionString",
                SaslPassword = Environment.GetEnvironmentVariable("CONNECTIONSTRING")
            };

            var producerBuilder = new ProducerBuilder<Null, string>(configKey);

            //IProducer<Null, string> producer = producerBuilder.SetOAuthBearerTokenRefreshHandler(OauthCallback).Build();
            IProducer<Null, string> producer = producerBuilder.Build();

            for (int x = 0; x < 10; x++)
            {
                var msg = new Message<Null, string> { Value = string.Format("This is a sample message - msg # {0} at {1}", x, DateTime.Now.ToString("yyyMMdd_HHmmSSfff")) };

                // publishes the message to Event Hubs
                var result = await producer.ProduceAsync(topicName, msg);

                Console.WriteLine($"Message {result.Value} sent to partition {result.TopicPartition} with result {result.Status}");
            }
            Console.WriteLine("Producer complete");
        }

    }
}

