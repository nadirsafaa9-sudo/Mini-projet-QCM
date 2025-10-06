const form = document.getElementById('quizForm');
const quizContainer = document.getElementById('quizContainer');
const scoreDiv = document.getElementById('score');

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  quizContainer.innerHTML = '';
  scoreDiv.innerHTML = '';

  const category = document.getElementById('category').value;
  const difficulty = document.getElementById('difficulty').value;
  const amount = document.getElementById('amount').value;

  // URL de l'API
  let apiUrl = `https://opentdb.com/api.php?amount=${amount}`;
  if (category) apiUrl += `&category=${category}`;
  if (difficulty) apiUrl += `&difficulty=${difficulty}`;
  apiUrl += '&type=multiple';

  const res = await fetch(apiUrl);
  const data = await res.json();

  startQuiz(data.results);
});

function startQuiz(questions) {
  let currentQuestion = 0;
  let score = 0;

  showQuestion(questions[currentQuestion]);

  function showQuestion(q) {
    quizContainer.innerHTML = '';
    
    const questionDiv = document.createElement('div');
    questionDiv.className = 'question';
    questionDiv.innerHTML = `<h3>${q.question}</h3>`;
    
    const answersDiv = document.createElement('div');
    answersDiv.className = 'answers';

    const answers = [...q.incorrect_answers, q.correct_answer];
    answers.sort(() => Math.random() - 0.5);

    answers.forEach(answer => {
      const btn = document.createElement('button');
      btn.innerHTML = answer;
      btn.addEventListener('click', () => {
        if(answer === q.correct_answer) {
          score++;
          btn.classList.add('correct');
        } else {
          btn.classList.add('wrong');
        }

        // Passer à la question suivante après 1s
        setTimeout(() => {
          currentQuestion++;
          if(currentQuestion < questions.length) {
            showQuestion(questions[currentQuestion]);
          } else {
            showScore();
          }
        }, 1000);
      });
      answersDiv.appendChild(btn);
    });

    quizContainer.appendChild(questionDiv);
    quizContainer.appendChild(answersDiv);
    
    // Timer pour chaque question
    let timer = 15;
    const timerDiv = document.createElement('div');
    timerDiv.innerHTML = `Temps restant: ${timer} s`;
    questionDiv.appendChild(timerDiv);

    const interval = setInterval(() => {
      timer--;
      timerDiv.innerHTML = `Temps restant: ${timer} s`;
      if(timer <= 0) {
        clearInterval(interval);
        currentQuestion++;
        if(currentQuestion < questions.length) {
          showQuestion(questions[currentQuestion]);
        } else {
          showScore();
        }
      }
    }, 1000);
  }

  function showScore() {
    scoreDiv.innerHTML = `Votre score: ${score} / ${questions.length}`;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    quizContainer.innerHTML = '';
  }
}

