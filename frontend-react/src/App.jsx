import { useState } from "react";
import {
  Box,
  Button,
  Grid,
  Heading,
  Input,
  Select,
  Text,
  Textarea,
  FormControl,
  FormLabel,
  Stack,
  Code,
  FormErrorMessage,
} from "@chakra-ui/react";
import { Formik, Form, Field } from "formik";
import * as Yup from "yup";
import {
  createAccount,
  getAccountById,
  getAccountsByUserId,
  debitAccount,
  creditAccount,
} from "./api/accounts.js";
import { registerUser, loginUser, getUserById } from "./api/users.js";
import { payLoan, getPaymentById } from "./api/payments.js";

export default function App() {
  // JWT token used by protected endpoints.
  const [authToken, setAuthToken] = useState("");

  // Output area state.
  const [output, setOutput] = useState("");
  const [error, setError] = useState("");

  function resetMessages() {
    setOutput("");
    setError("");
  }

  const authSchema = Yup.object({
    username: Yup.string().required("Username is required"),
    password: Yup.string().min(4, "Minimum 4 characters").required("Password is required"),
  });

  const idSchema = Yup.object({
    userId: Yup.number().typeError("User ID must be a number").required("User ID is required"),
  });

  const accountIdSchema = Yup.object({
    accountId: Yup.number().typeError("Account ID must be a number").required("Account ID is required"),
  });

  const paymentIdSchema = Yup.object({
    paymentId: Yup.number().typeError("Payment ID must be a number").required("Payment ID is required"),
  });

  const createAccountSchema = Yup.object({
    userId: Yup.number().typeError("User ID must be a number").required("User ID is required"),
    accountType: Yup.string().oneOf(["DEPOSIT", "LOAN"]).required("Account type is required"),
    initialDeposit: Yup.number()
      .typeError("Initial deposit must be a number")
      .min(0, "Deposit cannot be negative")
      .required("Initial deposit is required"),
  });

  const amountSchema = Yup.object({
    accountId: Yup.number().typeError("Account ID must be a number").required("Account ID is required"),
    amount: Yup.number()
      .typeError("Amount must be a number")
      .positive("Amount must be positive")
      .required("Amount is required"),
  });

  const loanPaymentSchema = Yup.object({
    loanAccountId: Yup.number().typeError("Loan account ID must be a number").required("Loan account ID is required"),
    depositAccountId: Yup.number()
      .typeError("Deposit account ID must be a number")
      .required("Deposit account ID is required"),
    amount: Yup.number()
      .typeError("Amount must be a number")
      .positive("Amount must be positive")
      .required("Amount is required"),
    description: Yup.string().max(120, "Description is too long"),
  });

  // Formik passes the form values as the first argument.
  async function handleRegister(values) {
    resetMessages();
    try {
      const res = await registerUser(values);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleLogin(values) {
    resetMessages();
    try {
      const token = await loginUser(values);
      setAuthToken(token);
      setOutput({ token });
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleGetUser(values) {
    resetMessages();
    try {
      const res = await getUserById(values.userId);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleCreateAccount(values) {
    resetMessages();
    try {
      const res = await createAccount(values, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleGetAccountById(values) {
    resetMessages();
    try {
      const res = await getAccountById(values.accountId, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleGetAccountsByUserId(values) {
    resetMessages();
    try {
      const res = await getAccountsByUserId(values.userId, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDebit(values) {
    resetMessages();
    try {
      const res = await debitAccount(values, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleCredit(values) {
    resetMessages();
    try {
      const res = await creditAccount(values, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handlePayLoan(values) {
    resetMessages();
    try {
      const res = await payLoan(values, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleGetPaymentById(values) {
    resetMessages();
    try {
      const res = await getPaymentById(values.paymentId, authToken);
      setOutput(res);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <Box minH="100vh" bg="gray.50" p={{ base: 4, md: 8 }}>
      <Box maxW="1200px" mx="auto">
        <Box bg="white" p={6} borderRadius="xl" boxShadow="sm" mb={6}>
          {/* Header section describing the app and showing the JWT token box */}
          <Text fontSize="sm" color="gray.500" letterSpacing="wide">
            Retail Loan Microservices
          </Text>
          <Heading size="lg" mt={2}>
            Loan Operations Console
          </Heading>
          <Text color="gray.600" mt={2}>
            A clean UI to demo user onboarding, account actions, and loan payments across microservices.
          </Text>
          <FormControl mt={4}>
            <FormLabel>JWT Token</FormLabel>
            <Textarea
              placeholder="Login to get a token"
              value={authToken}
              onChange={(e) => setAuthToken(e.target.value)}
              rows={3}
            />
            <Text fontSize="xs" color="gray.500" mt={1}>
              Paste a token here if you already have one.
            </Text>
          </FormControl>
        </Box>

        {/* Responsive grid: 1 column on mobile, 2 on tablet, 3 on desktop */}
        <Grid templateColumns={{ base: "1fr", md: "1fr 1fr", xl: "1fr 1fr 1fr" }} gap={6}>
          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Register User
            </Heading>
            {/* Formik manages the form state and passes values to handleRegister */}
            <Formik
              initialValues={{ username: "", password: "" }}
              validationSchema={authSchema}
              onSubmit={handleRegister}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="username">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Username</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="password">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Password</FormLabel>
                          <Input type="password" {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Register
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Login
            </Heading>
            {/* Login form returns a JWT token */}
            <Formik
              initialValues={{ username: "", password: "" }}
              validationSchema={authSchema}
              onSubmit={handleLogin}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="username">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Username</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="password">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Password</FormLabel>
                          <Input type="password" {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Login
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Get User By ID
            </Heading>
            {/* Lookup a user by ID */}
            <Formik initialValues={{ userId: "" }} validationSchema={idSchema} onSubmit={handleGetUser}>
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="userId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>User ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Fetch User
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Create Account
            </Heading>
            {/* Create either a DEPOSIT or LOAN account */}
            <Formik
              initialValues={{ userId: "", accountType: "DEPOSIT", initialDeposit: "" }}
              validationSchema={createAccountSchema}
              onSubmit={handleCreateAccount}
            >
              {({ isSubmitting, values, setFieldValue }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="userId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>User ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <FormControl>
                      <FormLabel>Account Type</FormLabel>
                      <Select
                        value={values.accountType}
                        onChange={(e) => setFieldValue("accountType", e.target.value)}
                      >
                        <option value="DEPOSIT">Deposit</option>
                        <option value="LOAN">Loan</option>
                      </Select>
                    </FormControl>
                    <Field name="initialDeposit">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Initial Deposit</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Create
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Get Account By ID
            </Heading>
            {/* Lookup a single account by ID */}
            <Formik
              initialValues={{ accountId: "" }}
              validationSchema={accountIdSchema}
              onSubmit={handleGetAccountById}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="accountId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Account ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Fetch Account
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Get Accounts By User ID
            </Heading>
            {/* Lookup all accounts for a user */}
            <Formik
              initialValues={{ userId: "" }}
              validationSchema={idSchema}
              onSubmit={handleGetAccountsByUserId}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="userId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>User ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Fetch Accounts
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Debit Account
            </Heading>
            {/* Debit money from a deposit account */}
            <Formik
              initialValues={{ accountId: "", amount: "" }}
              validationSchema={amountSchema}
              onSubmit={handleDebit}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="accountId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Account ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="amount">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Amount</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Debit
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Credit Account
            </Heading>
            {/* Credit money into a deposit account */}
            <Formik
              initialValues={{ accountId: "", amount: "" }}
              validationSchema={amountSchema}
              onSubmit={handleCredit}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="accountId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Account ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="amount">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Amount</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Credit
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Pay Loan
            </Heading>
            {/* Pay a loan using a deposit account */}
            <Formik
              initialValues={{ loanAccountId: "", depositAccountId: "", amount: "", description: "" }}
              validationSchema={loanPaymentSchema}
              onSubmit={handlePayLoan}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="loanAccountId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Loan Account ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="depositAccountId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Deposit Account ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="amount">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Amount</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Field name="description">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Description</FormLabel>
                          <Input {...field} />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Pay Loan
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>

          <Box bg="white" p={5} borderRadius="xl" boxShadow="sm">
            <Heading size="md" mb={4}>
              Get Payment By ID
            </Heading>
            {/* Lookup a payment record */}
            <Formik
              initialValues={{ paymentId: "" }}
              validationSchema={paymentIdSchema}
              onSubmit={handleGetPaymentById}
            >
              {({ isSubmitting }) => (
                <Form>
                  <Stack spacing={3}>
                    <Field name="paymentId">
                      {({ field, meta }) => (
                        <FormControl isInvalid={meta.touched && meta.error}>
                          <FormLabel>Payment ID</FormLabel>
                          <Input {...field} required />
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                        </FormControl>
                      )}
                    </Field>
                    <Button type="submit" colorScheme="blue" isLoading={isSubmitting}>
                      Fetch Payment
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </Box>
        </Grid>

        {/* Output panel for API responses and errors */}
        <Box bg="white" p={5} borderRadius="xl" boxShadow="sm" mt={6}>
          <Heading size="md" mb={3}>
            Output
          </Heading>
          {error ? (
            <Code colorScheme="red" p={3} display="block" whiteSpace="pre-wrap">
              {error}
            </Code>
          ) : (
            <Code p={3} display="block" whiteSpace="pre-wrap">
              {output ? JSON.stringify(output, null, 2) : "Run an action to see output."}
            </Code>
          )}
        </Box>
      </Box>
    </Box>
  );
}
