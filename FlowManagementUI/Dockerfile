FROM python
WORKDIR /app
COPY . /app
# RUN python -m pip install --trusted-host files.pythonhosted.org --trusted-host pypi.org --trusted-host pypi.python.org --upgrade pip
RUN pip install --trusted-host=pypi.org --trusted-host=files.pythonhosted.org -r requirements.txt
# RUN pip install -r requirements.txt
CMD ["python3", "app.py"]